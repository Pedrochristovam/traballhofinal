package br.com.sigevi.pattern.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern: Subject que notifica todos os listeners registrados.
 */
@Component
public class AuditoriaPublisher {

    private final List<AuditoriaListener> listeners = new ArrayList<>();

    public void subscribe(AuditoriaListener listener) {
        listeners.add(listener);
    }

    public void publish(AuditoriaEvent event) {
        listeners.forEach(listener -> listener.onAuditoriaEvent(event));
    }
}
