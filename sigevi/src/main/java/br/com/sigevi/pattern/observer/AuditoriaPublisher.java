package br.com.sigevi.pattern.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Quando muda algo no sistema, avisa quem tá escutando. */
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
