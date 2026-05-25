package br.com.sigevi.service;

import br.com.sigevi.dto.request.UsuarioRequest;
import br.com.sigevi.exception.BusinessException;
import br.com.sigevi.mapper.UsuarioMapper;
import br.com.sigevi.model.Usuario;
import br.com.sigevi.model.enums.RoleUsuario;
import br.com.sigevi.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuditoriaService auditoriaService;
    @InjectMocks private UsuarioService usuarioService;

    @Test
    void deveLancarExcecaoQuandoEmailJaExiste() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNome("Teste");
        request.setEmail("teste@sigevi.com");
        request.setSenha("Senha@123");
        request.setRole(RoleUsuario.USER);

        when(usuarioRepository.existsByEmail("teste@sigevi.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> usuarioService.cadastrar(request));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {
        UsuarioRequest request = new UsuarioRequest();
        request.setNome("Teste");
        request.setEmail("novo@sigevi.com");
        request.setSenha("Senha@123");
        request.setRole(RoleUsuario.USER);

        Usuario usuario = Usuario.builder().id(1L).email("novo@sigevi.com").ativo(true).build();

        when(usuarioRepository.existsByEmail("novo@sigevi.com")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("hash");
        when(usuarioMapper.toEntity(request, "hash")).thenReturn(usuario);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        when(usuarioMapper.toResponse(usuario)).thenReturn(
                br.com.sigevi.dto.response.UsuarioResponse.builder().id(1L).email("novo@sigevi.com").build());

        usuarioService.cadastrar(request);

        verify(usuarioRepository).save(usuario);
        verify(auditoriaService).registrar(any(), any(), any(), any(), any(), any());
    }
}
