package br.com.sigevi.dto.response;

import br.com.sigevi.model.enums.RoleUsuario;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private RoleUsuario role;
    private boolean ativo;
    private LocalDateTime criadoEm;
}
