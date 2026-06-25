package br.com.sigevi.controller;

import br.com.sigevi.dto.request.UsuarioRequest;
import br.com.sigevi.dto.response.UsuarioResponse;
import br.com.sigevi.security.SecurityUtils;
import br.com.sigevi.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final SecurityUtils securityUtils;

    public UsuarioController(UsuarioService usuarioService, SecurityUtils securityUtils) {
        this.usuarioService = usuarioService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cadastrar usuario (ADMIN)")
    public ResponseEntity<UsuarioResponse> cadastrar(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.cadastrar(request, securityUtils.getUsuarioLogadoId()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar usuario (ADMIN)")
    public ResponseEntity<UsuarioResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.atualizar(id, request, securityUtils.getUsuarioLogadoId()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar usuarios ativos")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        return ResponseEntity.ok(usuarioService.listarAtivos());
    }

    @PatchMapping("/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desativar usuario (ADMIN)")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        usuarioService.desativar(id, securityUtils.getUsuarioLogadoId());
        return ResponseEntity.noContent().build();
    }
}
