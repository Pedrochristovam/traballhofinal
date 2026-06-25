package br.com.sigevi.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MinhaCargaResponse {

    private long agendadas;
    private long emAndamento;
    private long atrasadas;
    private long concluidas;
    private List<VistoriaOperacionalResponse> proximasVistorias;
}
