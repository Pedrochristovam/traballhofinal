package br.com.sigevi.dto.request;

import br.com.sigevi.model.enums.TipoRelatorio;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelatorioRequest {

    @NotNull(message = "Tipo de relatorio e obrigatorio")
    private TipoRelatorio tipo;
}
