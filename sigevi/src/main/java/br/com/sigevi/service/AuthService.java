package br.com.sigevi.service;

import br.com.sigevi.dto.request.LoginRequest;
import br.com.sigevi.dto.response.LoginResponse;
import br.com.sigevi.dto.response.UsuarioResponse;
import br.com.sigevi.exception.UnauthorizedException;
import br.com.sigevi.mapper.UsuarioMapper;
import br.com.sigevi.model.enums.AcaoAuditoria;
import br.com.sigevi.repository.UsuarioRepository;
import br.com.sigevi.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Login: confere senha, gera token e registra na auditoria. */
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final AuditoriaService auditoriaService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserDetailsService userDetailsService,
                       JwtTokenProvider jwtTokenProvider,
                       UsuarioRepository usuarioRepository,
                       UsuarioMapper usuarioMapper,
                       AuditoriaService auditoriaService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.auditoriaService = auditoriaService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));
        } catch (Exception e) {
            throw new UnauthorizedException("Credenciais invalidas");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtTokenProvider.generateToken(userDetails); // aqui nasce o token pro Swagger/Postman

        var usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Usuario nao encontrado"));

        auditoriaService.registrar("Usuario", usuario.getId(), AcaoAuditoria.LOGIN,
                null, "Login realizado", usuario.getId());

        UsuarioResponse usuarioResponse = usuarioMapper.toResponse(usuario);

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .expiresIn(86400L)
                .usuario(usuarioResponse)
                .build();
    }
}
