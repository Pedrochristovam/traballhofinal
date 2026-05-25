package br.com.sigevi.controller;

import br.com.sigevi.dto.request.RelatorioRequest;
import br.com.sigevi.dto.response.RelatorioResponse;
import br.com.sigevi.model.Relatorio;
import br.com.sigevi.repository.RelatorioRepository;
import br.com.sigevi.security.SecurityUtils;
import br.com.sigevi.service.RelatorioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/relatorios")
@Tag(name = "Relatorios")
@SecurityRequirement(name = "bearerAuth")
public class RelatorioController {

    private final RelatorioService relatorioService;
    private final RelatorioRepository relatorioRepository;
    private final SecurityUtils securityUtils;

    public RelatorioController(RelatorioService relatorioService,
                               RelatorioRepository relatorioRepository,
                               SecurityUtils securityUtils) {
        this.relatorioService = relatorioService;
        this.relatorioRepository = relatorioRepository;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/vistoria/{vistoriaId}")
    @Operation(summary = "Gerar relatorio PDF (resumido ou completo)")
    public ResponseEntity<RelatorioResponse> gerar(@PathVariable Long vistoriaId,
                                                 @Valid @RequestBody RelatorioRequest request) {
        return ResponseEntity.ok(relatorioService.gerar(vistoriaId, request, securityUtils.getUsuarioLogadoId()));
    }

    @GetMapping("/vistoria/{vistoriaId}")
    @Operation(summary = "Listar relatorios da vistoria")
    public ResponseEntity<List<RelatorioResponse>> listar(@PathVariable Long vistoriaId) {
        return ResponseEntity.ok(relatorioService.listarPorVistoria(vistoriaId));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download do relatorio PDF")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        Resource resource = relatorioService.download(id);
        Relatorio relatorio = relatorioRepository.findById(id).orElseThrow();
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=relatorio-" + id + ".pdf")
                .body(resource);
    }
}
