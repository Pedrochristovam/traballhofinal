package br.com.sigevi.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InspetorProdutividadeResponse {

    private Long inspetorId;
    private String inspetorNome;
    private long totalVistorias;
    private long concluidas;
    private long pendentes;
}
