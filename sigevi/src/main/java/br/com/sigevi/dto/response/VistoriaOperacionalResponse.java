package br.com.sigevi.dto.response;

import br.com.sigevi.model.enums.StatusVistoria;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class VistoriaOperacionalResponse {

    private Long id;
    private Long imovelId;
    private String imovelMatricula;
    private String imovelEndereco;
    private Long inspetorId;
    private String inspetorNome;
    private StatusVistoria status;
    private LocalDate dataVistoria;
    private Long diasAtraso;
    private boolean atrasada;
}
