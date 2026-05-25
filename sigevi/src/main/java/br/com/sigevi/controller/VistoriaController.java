package br.com.sigevi.controller;

import br.com.sigevi.dto.request.ObservacaoRequest;
import br.com.sigevi.dto.request.StatusVistoriaRequest;
import br.com.sigevi.dto.request.VistoriaRequest;
import br.com.sigevi.dto.response.VistoriaResponse;
import br.com.sigevi.security.SecurityUtils;
import br.com.sigevi.service.VistoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vistorias")
@Tag(name = "Vistorias")
@SecurityRequirement(name = "bearerAuth")
public class VistoriaController {

    private final VistoriaService vistoriaService;
    private final SecurityUtils securityUtils;

    public VistoriaController(VistoriaService vistoriaService, SecurityUtils securityUtils) {
        this.vistoriaService = vistoriaService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    @Operation(summary = "Criar vistoria vinculada a imovel")
    public ResponseEntity<VistoriaResponse> criar(@Valid @RequestBody VistoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vistoriaService.criar(request, securityUtils.getUsuarioLogadoId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar vistoria por ID")
    public ResponseEntity<VistoriaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(vistoriaService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Alterar status da vistoria")
    public ResponseEntity<VistoriaResponse> alterarStatus(@PathVariable Long id,
                                                          @Valid @RequestBody StatusVistoriaRequest request) {
        return ResponseEntity.ok(vistoriaService.alterarStatus(id, request, securityUtils.getUsuarioLogadoId()));
    }

    @PatchMapping("/{id}/observacoes")
    @Operation(summary = "Adicionar observacoes")
    public ResponseEntity<VistoriaResponse> observacoes(@PathVariable Long id,
                                                        @Valid @RequestBody ObservacaoRequest request) {
        return ResponseEntity.ok(vistoriaService.adicionarObservacoes(id, request, securityUtils.getUsuarioLogadoId()));
    }

    @GetMapping("/imovel/{imovelId}")
    @Operation(summary = "Listar vistorias por imovel")
    public ResponseEntity<List<VistoriaResponse>> listarPorImovel(@PathVariable Long imovelId) {
        return ResponseEntity.ok(vistoriaService.listarPorImovel(imovelId));
    }
}
