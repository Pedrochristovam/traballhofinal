package br.com.sigevi.config;

import br.com.sigevi.model.Usuario;
import br.com.sigevi.model.enums.RoleUsuario;
import br.com.sigevi.repository.UsuarioRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Na primeira subida da API, cria o admin padrão se num existir ainda.
 * Senha já vai criptografada — login: admin@sigevi.com / Admin@123
 */
@Component
@Profile("!test")
public class AdminSeedConfig implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminSeedConfig(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        // só cria uma vez — se já tiver admin no banco, num mexe
        if (!usuarioRepository.existsByEmail("admin@sigevi.com")) {
            Usuario admin = Usuario.builder()
                    .nome("Administrador SIGEVI")
                    .email("admin@sigevi.com")
                    .senha(passwordEncoder.encode("Admin@123"))
                    .role(RoleUsuario.ADMIN)
                    .ativo(true)
                    .build();
            usuarioRepository.save(admin);
        }
    }
}
