package br.com.sigevi.pattern.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer Pattern — o "radinho" que avisa todo mundo quando rola alteração.
 * Quem quiser ouvir (tipo o listener que grava no banco), se inscreve no subscribe.
 */
@Component
public class AuditoriaPublisher {

    private final List<AuditoriaListener> listeners = new ArrayList<>();

    public void subscribe(AuditoriaListener listener) {
        listeners.add(listener);
    }

    // dispara o evento pros ouvintes — tipo gritar "ó, mudou o trem aqui!"
    public void publish(AuditoriaEvent event) {
        listeners.forEach(listener -> listener.onAuditoriaEvent(event));
    }
}
