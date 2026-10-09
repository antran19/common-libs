package com.nexus.common.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    private final JwtTokenProvider jwtTokenProvider = newProvider();

    private static JwtTokenProvider newProvider() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-must-be-at-least-256-bits-long-for-hs256!!");
        properties.setExpirationMinutes(60);
        return new JwtTokenProvider(properties);
    }

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void authenticates_whenNoTokenBlacklistPortIsWired() throws Exception {
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider, Optional.empty());
        String token = jwtTokenProvider.generateToken("user-1", "BUYER", List.of("AUTH.LOGIN"), "TRUSTED");

        filter.doFilterInternal(requestWithToken(token), new MockHttpServletResponse(), (req, res) -> {});

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo("user-1");
    }

    @Test
    void doesNotAuthenticate_whenTokenJtiIsBlacklisted() throws Exception {
        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtTokenProvider, Optional.of(jti -> true));
        String token = jwtTokenProvider.generateToken("user-1", "BUYER", List.of("AUTH.LOGIN"), "TRUSTED");

        filter.doFilterInternal(requestWithToken(token), new MockHttpServletResponse(), (req, res) -> {});

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void authenticates_whenTokenJtiIsNotBlacklisted() throws Exception {
        JwtAuthenticationFilter filter =
                new JwtAuthenticationFilter(jwtTokenProvider, Optional.of(jti -> false));
        String token = jwtTokenProvider.generateToken("user-1", "BUYER", List.of("AUTH.LOGIN"), "TRUSTED");

        filter.doFilterInternal(requestWithToken(token), new MockHttpServletResponse(), (req, res) -> {});

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    private MockHttpServletRequest requestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }
}
