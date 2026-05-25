package br.com.sigevi.dto.request;

import br.com.sigevi.model.enums.RoleUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {

    @NotBlank(message = "Nome e obrigatorio")
    @Size(max = 150)
    private String nome;

    @NotBlank(message = "Email e obrigatorio")
    @Email(message = "Email invalido")
    private String email;

    @Size(min = 8, message = "Senha deve ter no minimo 8 caracteres")
    private String senha;

    @NotNull(message = "Role e obrigatoria")
    private RoleUsuario role;
}
