package br.com.sigevi.dto.request;

import br.com.sigevi.model.enums.TipoImovel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ImovelRequest {

    @NotBlank(message = "Matricula e obrigatoria")
    @Size(max = 50)
    private String matricula;

    @NotBlank(message = "Endereco e obrigatorio")
    private String endereco;

    @NotBlank(message = "Cidade e obrigatoria")
    private String cidade;

    @NotBlank(message = "Estado e obrigatorio")
    @Size(min = 2, max = 2)
    private String estado;

    @NotBlank(message = "CEP e obrigatorio")
    private String cep;

    @NotNull(message = "Tipo e obrigatorio")
    private TipoImovel tipo;

    private BigDecimal areaM2;
    private String descricao;
}
