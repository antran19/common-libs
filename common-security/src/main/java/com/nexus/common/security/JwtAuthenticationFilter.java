package com.nexus.common.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// common-security is consumed by both servlet-based services (user-service,
// catalog-service) and the reactive (WebFlux) api-gateway. This class extends a
// Servlet Filter base class, which requires jakarta.servlet.Filter on the runtime
// classpath merely to be scanned by Spring's ConfigurationClassParser (it walks the
// interface hierarchy of every component-scanned class, servlet or not). api-gateway
// has no servlet container (it depends on spring-cloud-starter-gateway/WebFlux, not
// spring-boot-starter-web), so jakarta.servlet-api is absent from its packaged jar and
// boot crashes with a FileNotFoundException for jakarta/servlet/Filter.class unless this
// bean is excluded up front. Gating on @ConditionalOnClass(jakarta.servlet.Filter) (rather
// than @ConditionalOnWebApplication(type = SERVLET)) matches the actual root cause: some
// catalog-service/user-service tests run with spring.main.web-application-type=none (no
// embedded server) while still being fundamentally servlet-based apps that need this
// filter wired for SecurityConfig's constructor injection to resolve, so gating on web
// application *type* incorrectly excluded the bean there too; gating on classpath
// presence of the Servlet API is correct in both the servlet-with-no-server-started test
// case and the truly reactive api-gateway case.
@ConditionalOnClass(name = "jakarta.servlet.Filter")
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtTokenProvider.isValid(token)) {
                Claims claims = jwtTokenProvider.parseClaims(token);
                String userId = claims.getSubject();
                @SuppressWarnings("unchecked")
                List<String> privileges = claims.get("privileges", List.class);
                List<SimpleGrantedAuthority> authorities = (privileges == null ? List.<String>of() : privileges)
                        .stream().map(SimpleGrantedAuthority::new).toList();

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(userId, null, authorities));
            }
        }
        filterChain.doFilter(request, response);
    }
}
