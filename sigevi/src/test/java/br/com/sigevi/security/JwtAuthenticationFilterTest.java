package br.com.sigevi.security;

import br.com.sigevi.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private UserDetailsService userDetailsService;
    @Mock private FilterChain filterChain;

    private ObjectMapper objectMapper;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        filter = new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService, objectMapper);
    }

    @Test
    void deveContinuarQuandoNaoHaHeaderAuthorization() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/imoveis");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void deveRetornar401QuandoTokenInvalido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/imoveis");
        request.addHeader("Authorization", "Bearer token-invalido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtTokenProvider.extractUsername("token-invalido"))
                .thenThrow(new MalformedJwtException("token invalido"));

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        ErrorResponse body = objectMapper.readValue(response.getContentAsByteArray(), ErrorResponse.class);
        assertEquals("Token invalido ou expirado", body.getMessage());
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void deveAutenticarQuandoTokenValido() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/imoveis");
        request.addHeader("Authorization", "Bearer token-valido");
        MockHttpServletResponse response = new MockHttpServletResponse();

        var userDetails = User.builder()
                .username("admin@sigevi.com")
                .password("hash")
                .authorities(List.of())
                .build();

        when(jwtTokenProvider.extractUsername("token-valido")).thenReturn("admin@sigevi.com");
        when(userDetailsService.loadUserByUsername("admin@sigevi.com")).thenReturn(userDetails);
        when(jwtTokenProvider.isTokenValid("token-valido", userDetails)).thenReturn(true);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
