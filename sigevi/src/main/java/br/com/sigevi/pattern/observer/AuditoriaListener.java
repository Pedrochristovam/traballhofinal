package br.com.sigevi.pattern.observer;

/**
 * Observer Pattern: interface do observador (ISP - contrato minimo).
 */
public interface AuditoriaListener {

    void onAuditoriaEvent(AuditoriaEvent event);
}
