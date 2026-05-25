package br.com.sigevi.mapper;

import br.com.sigevi.dto.request.UsuarioRequest;
import br.com.sigevi.dto.response.UsuarioResponse;
import br.com.sigevi.model.Usuario;
import org.springframework.stereotype.Component;

/**
 * DTO Pattern: converte entre entidade de dominio e objetos de transferencia.
 */
@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .ativo(usuario.isAtivo())
                .criadoEm(usuario.getCriadoEm())
                .build();
    }

    public Usuario toEntity(UsuarioRequest request, String senhaEncoded) {
        return Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(senhaEncoded)
                .role(request.getRole())
                .ativo(true)
                .build();
    }

    public void updateEntity(Usuario usuario, UsuarioRequest request) {
        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());
        usuario.setRole(request.getRole());
    }
}
