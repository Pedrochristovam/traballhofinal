package br.com.sigevi.mapper;

import br.com.sigevi.dto.response.AuditoriaResponse;
import br.com.sigevi.model.Auditoria;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaMapper {

    public AuditoriaResponse toResponse(Auditoria auditoria) {
        return AuditoriaResponse.builder()
                .id(auditoria.getId())
                .entidade(auditoria.getEntidade())
                .entidadeId(auditoria.getEntidadeId())
                .acao(auditoria.getAcao())
                .valorAnterior(auditoria.getValorAnterior())
                .valorNovo(auditoria.getValorNovo())
                .usuarioId(auditoria.getUsuario() != null ? auditoria.getUsuario().getId() : null)
                .usuarioNome(auditoria.getUsuario() != null ? auditoria.getUsuario().getNome() : null)
                .criadoEm(auditoria.getCriadoEm())
                .build();
    }
}
