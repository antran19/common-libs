package com.nexus.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-must-be-at-least-256-bits-long-for-hs256!!");
        properties.setExpirationMinutes(60);
        provider = new JwtTokenProvider(properties);
    }

    @Test
    void generateThenParse_roundTripsClaims() {
        String token = provider.generateToken("user-1", "BUYER", List.of("AUTH.LOGIN", "PROFILE.VIEW"));

        Claims claims = provider.parseClaims(token);

        assertThat(claims.getSubject()).isEqualTo("user-1");
        assertThat(claims.get("role", String.class)).isEqualTo("BUYER");
        assertThat(claims.get("privileges", List.class)).containsExactly("AUTH.LOGIN", "PROFILE.VIEW");
    }

    @Test
    void isValid_returnsTrueForFreshToken() {
        String token = provider.generateToken("user-1", "BUYER", List.of("AUTH.LOGIN"));
        assertThat(provider.isValid(token)).isTrue();
    }

    @Test
    void isValid_returnsFalseForTamperedToken() {
        String token = provider.generateToken("user-1", "BUYER", List.of("AUTH.LOGIN"));
        String tampered = token.substring(0, token.length() - 2) + "xx";
        assertThat(provider.isValid(tampered)).isFalse();
    }

    @Test
    void parseClaims_throwsForGarbageToken() {
        assertThatThrownBy(() -> provider.parseClaims("not-a-jwt")).isInstanceOf(JwtException.class);
    }
}
