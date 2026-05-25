package br.com.sigevi.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private String token;
    private String tipo;
    private Long expiresIn;
    private UsuarioResponse usuario;
}
