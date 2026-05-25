package br.com.sigevi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObservacaoRequest {

    @NotBlank(message = "Observacao e obrigatoria")
    private String observacoes;
}
