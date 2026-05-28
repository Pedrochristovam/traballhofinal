package br.com.sigevi.pattern.factory;

import br.com.sigevi.model.Auditoria;
import br.com.sigevi.model.Usuario;
import br.com.sigevi.model.enums.AcaoAuditoria;
import br.com.sigevi.pattern.observer.AuditoriaEvent;

public final class AuditoriaFactory {

    private AuditoriaFactory() {
    }

    public static Auditoria fromEvent(AuditoriaEvent event, Usuario usuario) {
        return Auditoria.builder()
                .entidade(event.getEntidade())
                .entidadeId(event.getEntidadeId())
                .acao(event.getAcao())
                .valorAnterior(event.getValorAnterior())
                .valorNovo(event.getValorNovo())
                .usuario(usuario)
                .build();
    }
}
