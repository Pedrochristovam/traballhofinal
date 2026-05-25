package br.com.sigevi.config;

import br.com.sigevi.security.JwtProperties;
import org.springframework.stereotype.Component;

/**
 * Singleton Pattern: instancia unica de configuracao JWT acessivel globalmente no contexto Spring.
 * Spring gerencia o ciclo de vida como singleton por padrao (@Component scope singleton).
 */
@Component
public class JwtPropertiesHolder {

    private static JwtPropertiesHolder instance;
    private final JwtProperties jwtProperties;

    public JwtPropertiesHolder(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        instance = this;
    }

    public static JwtPropertiesHolder getInstance() {
        return instance;
    }

    public JwtProperties getProperties() {
        return jwtProperties;
    }
}
