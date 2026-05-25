package br.com.sigevi.service;

import br.com.sigevi.dto.response.AuditoriaResponse;
import br.com.sigevi.mapper.AuditoriaMapper;
import br.com.sigevi.model.enums.AcaoAuditoria;
import br.com.sigevi.pattern.observer.AuditoriaEvent;
import br.com.sigevi.pattern.observer.AuditoriaPublisher;
import br.com.sigevi.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final AuditoriaMapper auditoriaMapper;
    private final AuditoriaPublisher auditoriaPublisher;

    public AuditoriaService(AuditoriaRepository auditoriaRepository,
                            AuditoriaMapper auditoriaMapper,
                            AuditoriaPublisher auditoriaPublisher) {
        this.auditoriaRepository = auditoriaRepository;
        this.auditoriaMapper = auditoriaMapper;
        this.auditoriaPublisher = auditoriaPublisher;
    }

    public void registrar(String entidade, Long entidadeId, AcaoAuditoria acao,
                          String valorAnterior, String valorNovo, Long usuarioId) {
        AuditoriaEvent event = AuditoriaEvent.builder()
                .entidade(entidade)
                .entidadeId(entidadeId)
                .acao(acao)
                .valorAnterior(valorAnterior)
                .valorNovo(valorNovo)
                .usuarioId(usuarioId)
                .build();
        auditoriaPublisher.publish(event);
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarHistorico(String entidade, Long entidadeId) {
        return auditoriaRepository.findByEntidadeAndEntidadeIdOrderByCriadoEmDesc(entidade, entidadeId)
                .stream()
                .map(auditoriaMapper::toResponse)
                .toList();
    }
}
