package br.com.sigevi.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties();
        props.setSecret("VGVzdFNlY3JldEtleUZvckpXVFRva2VuR2VuZXJhdGlvbjEyMzQ1Njc4OTA=");
        props.setExpirationMs(3600000);
        jwtTokenProvider = new JwtTokenProvider(props);
    }

    @Test
    void deveGerarEValidarToken() {
        var user = User.builder()
                .username("teste@sigevi.com")
                .password("hash")
                .authorities(List.of())
                .build();

        String token = jwtTokenProvider.generateToken(user);
        assertNotNull(token);
        assertEquals("teste@sigevi.com", jwtTokenProvider.extractUsername(token));
        assertTrue(jwtTokenProvider.isTokenValid(token, user));
    }
}
