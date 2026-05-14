package com.setas.setas_backend.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    // Mínimo 32 bytes para HS256
    private static final String SECRET = "secretkeyfortestingpurposesonly12";
    private static final long EXPIRATION = 3_600_000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", SECRET);
        ReflectionTestUtils.setField(jwtService, "EXPIRATION_TIME", EXPIRATION);
    }

    @Test
    void generateToken_deberiaRetornarTokenNoVacio() {
        String token = jwtService.generateToken("andy@test.com", "CLIENT");

        assertThat(token).isNotBlank();
    }

    @Test
    void extractEmail_deberiaRetornarEmailCorrecto() {
        String token = jwtService.generateToken("andy@test.com", "CLIENT");

        assertThat(jwtService.extractEmail(token)).isEqualTo("andy@test.com");
    }

    @Test
    void extractRole_deberiaRetornarRolCorrecto() {
        String token = jwtService.generateToken("andy@test.com", "ADMIN");

        assertThat(jwtService.extractRole(token)).isEqualTo("ADMIN");
    }

    @Test
    void isTokenValid_deberiaRetornarTrueParaTokenValido() {
        String token = jwtService.generateToken("andy@test.com", "CLIENT");

        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    void isTokenValid_deberiaRetornarFalseParaTokenManipulado() {
        String token = jwtService.generateToken("andy@test.com", "CLIENT");
        String tampered = token.substring(0, token.length() - 5) + "XXXXX";

        assertThat(jwtService.isTokenValid(tampered)).isFalse();
    }
}
