package br.com.sigevi.dto.response;

import br.com.sigevi.model.enums.StatusVistoria;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class VistoriaResponse {

    private Long id;
    private Long imovelId;
    private String imovelMatricula;
    private Long inspetorId;
    private String inspetorNome;
    private StatusVistoria status;
    private LocalDate dataVistoria;
    private String observacoes;
    private LocalDateTime criadoEm;
}
