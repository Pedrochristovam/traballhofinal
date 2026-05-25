package br.com.sigevi.dto.request;

import br.com.sigevi.model.enums.StatusVistoria;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusVistoriaRequest {

    @NotNull(message = "Status e obrigatorio")
    private StatusVistoria status;
}
