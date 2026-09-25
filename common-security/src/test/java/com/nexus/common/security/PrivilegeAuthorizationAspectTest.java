package com.nexus.common.security;

import com.nexus.common.core.exception.ForbiddenException;
import com.nexus.common.core.exception.UnauthorizedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(PrivilegeAuthorizationAspectTest.TestConfig.class)
class PrivilegeAuthorizationAspectTest {

    @Configuration
    @EnableAspectJAutoProxy
    static class TestConfig {
        @Bean
        PrivilegeAuthorizationAspect aspect() { return new PrivilegeAuthorizationAspect(); }

        @Bean
        ProtectedService protectedService() { return new ProtectedService(); }
    }

    @Component
    static class ProtectedService {
        @RequiresPrivilege("PROFILE.CHANGE_PASSWORD")
        public String doSensitiveThing() { return "done"; }
    }

    @Autowired
    private ProtectedService protectedService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void allowsCallWhenAuthorityPresent() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user-1", null,
                        List.of(new SimpleGrantedAuthority("PROFILE.CHANGE_PASSWORD"))));

        assertThat(protectedService.doSensitiveThing()).isEqualTo("done");
    }

    @Test
    void rejectsCallWhenAuthorityMissing() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user-1", null,
                        List.of(new SimpleGrantedAuthority("PROFILE.VIEW"))));

        assertThatThrownBy(() -> protectedService.doSensitiveThing()).isInstanceOf(ForbiddenException.class);
    }

    @Test
    void rejectsCallWhenNotAuthenticated() {
        assertThatThrownBy(() -> protectedService.doSensitiveThing()).isInstanceOf(UnauthorizedException.class);
    }
}
