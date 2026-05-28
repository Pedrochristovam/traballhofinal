package br.com.sigevi.pattern.observer;

import br.com.sigevi.model.enums.AcaoAuditoria;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditoriaEvent {

    private String entidade;
    private Long entidadeId;
    private AcaoAuditoria acao;
    private String valorAnterior;
    private String valorNovo;
    private Long usuarioId;
}
