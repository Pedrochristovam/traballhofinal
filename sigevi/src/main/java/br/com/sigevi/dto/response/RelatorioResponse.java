package br.com.sigevi.dto.response;

import br.com.sigevi.model.enums.TipoRelatorio;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RelatorioResponse {

    private Long id;
    private Long vistoriaId;
    private TipoRelatorio tipo;
    private String caminhoArquivo;
    private Long geradoPorId;
    private LocalDateTime criadoEm;
}
