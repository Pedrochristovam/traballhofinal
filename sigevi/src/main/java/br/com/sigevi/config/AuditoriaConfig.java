package br.com.sigevi.config;

import br.com.sigevi.pattern.observer.AuditoriaPublisher;
import br.com.sigevi.service.AuditoriaPersistListener;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class AuditoriaConfig {

    private final AuditoriaPublisher publisher;
    private final AuditoriaPersistListener persistListener;

    public AuditoriaConfig(AuditoriaPublisher publisher, AuditoriaPersistListener persistListener) {
        this.publisher = publisher;
        this.persistListener = persistListener;
    }

    @PostConstruct
    public void registerListeners() {
        publisher.subscribe(persistListener);
    }
}
