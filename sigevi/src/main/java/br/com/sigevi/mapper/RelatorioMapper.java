package br.com.sigevi.mapper;

import br.com.sigevi.dto.response.RelatorioResponse;
import br.com.sigevi.model.Relatorio;

public final class RelatorioMapper {

    private RelatorioMapper() {
    }

    public static RelatorioResponse toResponse(Relatorio relatorio, String baseUrl) {
        return RelatorioResponse.builder()
                .id(relatorio.getId())
                .vistoriaId(relatorio.getVistoria().getId())
                .tipo(relatorio.getTipo())
                .url(baseUrl + "/relatorios/" + relatorio.getId() + "/download")
                .geradoPorId(relatorio.getGeradoPor().getId())
                .criadoEm(relatorio.getCriadoEm())
                .build();
    }
}
