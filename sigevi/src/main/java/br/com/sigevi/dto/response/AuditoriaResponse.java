package br.com.sigevi.dto.response;

import br.com.sigevi.model.enums.AcaoAuditoria;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuditoriaResponse {

    private Long id;
    private String entidade;
    private Long entidadeId;
    private AcaoAuditoria acao;
    private String valorAnterior;
    private String valorNovo;
    private Long usuarioId;
    private String usuarioNome;
    private LocalDateTime criadoEm;
}
