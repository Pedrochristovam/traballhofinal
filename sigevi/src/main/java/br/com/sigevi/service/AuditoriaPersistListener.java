package br.com.sigevi.service;

import br.com.sigevi.pattern.factory.AuditoriaFactory;
import br.com.sigevi.pattern.observer.AuditoriaEvent;
import br.com.sigevi.pattern.observer.AuditoriaListener;
import br.com.sigevi.repository.AuditoriaRepository;
import br.com.sigevi.repository.UsuarioRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Observer Pattern: listener que persiste eventos de auditoria.
 */
@Component
public class AuditoriaPersistListener implements AuditoriaListener {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public AuditoriaPersistListener(AuditoriaRepository auditoriaRepository,
                                    UsuarioRepository usuarioRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public void onAuditoriaEvent(AuditoriaEvent event) {
        var usuario = event.getUsuarioId() != null
                ? usuarioRepository.findById(event.getUsuarioId()).orElse(null)
                : null;
        auditoriaRepository.save(AuditoriaFactory.fromEvent(event, usuario));
    }
}
