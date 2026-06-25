package br.com.sigevi.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ResumoOperacionalResponse {

    private long totalVistorias;
    private long totalImoveis;
    private long vistoriasAtivas;
    private long vistoriasAtrasadas;
    private long concluidasSemana;
    private long concluidasMes;
    private Map<String, Long> totalPorStatus;
}
