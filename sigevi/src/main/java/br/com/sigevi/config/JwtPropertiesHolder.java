package br.com.sigevi.config;

import br.com.sigevi.security.JwtProperties;
import org.springframework.stereotype.Component;

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
