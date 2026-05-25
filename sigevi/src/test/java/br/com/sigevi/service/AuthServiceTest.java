package br.com.sigevi.service;

import br.com.sigevi.dto.request.LoginRequest;
import br.com.sigevi.exception.UnauthorizedException;
import br.com.sigevi.mapper.UsuarioMapper;
import br.com.sigevi.model.Usuario;
import br.com.sigevi.model.enums.RoleUsuario;
import br.com.sigevi.repository.UsuarioRepository;
import br.com.sigevi.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private AuditoriaService auditoriaService;
    @InjectMocks private AuthService authService;

    @Test
    void deveFazerLoginComSucesso() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@sigevi.com");
        request.setSenha("Admin@123");

        Usuario usuario = Usuario.builder()
                .id(1L).email("admin@sigevi.com").nome("Admin").role(RoleUsuario.ADMIN).ativo(true).build();

        when(userDetailsService.loadUserByUsername("admin@sigevi.com"))
                .thenReturn(User.builder().username("admin@sigevi.com").password("hash")
                        .authorities(List.of()).build());
        when(jwtTokenProvider.generateToken(any())).thenReturn("jwt-token");
        when(usuarioRepository.findByEmail("admin@sigevi.com")).thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponse(usuario)).thenReturn(
                br.com.sigevi.dto.response.UsuarioResponse.builder().id(1L).email("admin@sigevi.com").build());

        var response = authService.login(request);

        assertEquals("jwt-token", response.getToken());
        assertEquals("Bearer", response.getTipo());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void deveRejeitarCredenciaisInvalidas() {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@sigevi.com");
        request.setSenha("errada");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid"));

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
    }
}
