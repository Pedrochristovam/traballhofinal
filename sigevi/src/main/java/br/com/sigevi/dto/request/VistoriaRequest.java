package br.com.sigevi.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VistoriaRequest {

    @NotNull(message = "Imovel e obrigatorio")
    private Long imovelId;

    @NotNull(message = "Inspetor e obrigatorio")
    private Long inspetorId;

    @NotNull(message = "Data da vistoria e obrigatoria")
    @FutureOrPresent(message = "Data da vistoria deve ser hoje ou futura")
    private LocalDate dataVistoria;

    private String observacoes;
}
