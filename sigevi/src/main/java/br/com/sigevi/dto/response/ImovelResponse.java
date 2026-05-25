package br.com.sigevi.dto.response;

import br.com.sigevi.model.enums.TipoImovel;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class ImovelResponse {

    private Long id;
    private String matricula;
    private String endereco;
    private String cidade;
    private String estado;
    private String cep;
    private TipoImovel tipo;
    private BigDecimal areaM2;
    private String descricao;
    private LocalDateTime criadoEm;
}
