package br.com.sigevi.service;

import br.com.sigevi.dto.request.UsuarioRequest;
import br.com.sigevi.dto.response.UsuarioResponse;
import br.com.sigevi.exception.BusinessException;
import br.com.sigevi.exception.ResourceNotFoundException;
import br.com.sigevi.mapper.UsuarioMapper;
import br.com.sigevi.model.Usuario;
import br.com.sigevi.model.enums.AcaoAuditoria;
import br.com.sigevi.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          UsuarioMapper usuarioMapper,
                          PasswordEncoder passwordEncoder,
                          AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public UsuarioResponse cadastrar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email ja cadastrado");
        }
        if (request.getSenha() == null || request.getSenha().length() < 8) {
            throw new BusinessException("Senha obrigatoria com minimo 8 caracteres");
        }
        Usuario usuario = usuarioMapper.toEntity(request, passwordEncoder.encode(request.getSenha()));
        usuario = usuarioRepository.save(usuario);
        auditoriaService.registrar("Usuario", usuario.getId(), AcaoAuditoria.CRIACAO,
                null, usuario.getEmail(), usuario.getId());
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);
        String emailAnterior = usuario.getEmail();
        if (!emailAnterior.equals(request.getEmail()) && usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email ja cadastrado");
        }
        usuarioMapper.updateEntity(usuario, request);
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(request.getSenha()));
        }
        usuario = usuarioRepository.save(usuario);
        auditoriaService.registrar("Usuario", id, AcaoAuditoria.ATUALIZACAO,
                emailAnterior, usuario.getEmail(), id);
        return usuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarAtivos() {
        return usuarioRepository.findByAtivoTrue().stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    @Transactional
    public void desativar(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuario.setAtivo(false);
        usuarioRepository.save(usuario);
        auditoriaService.registrar("Usuario", id, AcaoAuditoria.ATUALIZACAO,
                "ativo=true", "ativo=false", id);
    }

    @Transactional(readOnly = true)
    public Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }
}
