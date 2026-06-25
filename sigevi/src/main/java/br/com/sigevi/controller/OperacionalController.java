package br.com.sigevi.controller;

import br.com.sigevi.dto.response.AuditoriaResponse;
import br.com.sigevi.dto.response.InspetorProdutividadeResponse;
import br.com.sigevi.dto.response.MinhaCargaResponse;
import br.com.sigevi.dto.response.ResumoOperacionalResponse;
import br.com.sigevi.dto.response.VistoriaOperacionalResponse;
import br.com.sigevi.model.enums.StatusVistoria;
import br.com.sigevi.security.SecurityUtils;
import br.com.sigevi.service.OperacionalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/operacional")
@Tag(name = "Operacional")
@SecurityRequirement(name = "bearerAuth")
public class OperacionalController {

    private final OperacionalService operacionalService;
    private final SecurityUtils securityUtils;

    public OperacionalController(OperacionalService operacionalService, SecurityUtils securityUtils) {
        this.operacionalService = operacionalService;
        this.securityUtils = securityUtils;
    }

    @GetMapping("/resumo")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Resumo operacional global (ADMIN)")
    public ResponseEntity<ResumoOperacionalResponse> resumo() {
        return ResponseEntity.ok(operacionalService.obterResumo());
    }

    @GetMapping("/minha-carga")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Carga de trabalho do inspetor logado")
    public ResponseEntity<MinhaCargaResponse> minhaCarga() {
        return ResponseEntity.ok(operacionalService.obterMinhaCarga(securityUtils.getUsuarioLogadoId()));
    }

    @GetMapping("/vistorias")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar vistorias com filtros operacionais (ADMIN)")
    public ResponseEntity<List<VistoriaOperacionalResponse>> listarVistorias(
            @RequestParam(required = false) StatusVistoria status,
            @RequestParam(required = false) Long inspetorId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(operacionalService.listarVistorias(status, inspetorId, dataInicio, dataFim));
    }

    @GetMapping("/vistorias/atrasadas")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar vistorias atrasadas (ADMIN)")
    public ResponseEntity<List<VistoriaOperacionalResponse>> listarAtrasadas() {
        return ResponseEntity.ok(operacionalService.listarAtrasadas());
    }

    @GetMapping("/inspetores/produtividade")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Produtividade por inspetor (ADMIN)")
    public ResponseEntity<List<InspetorProdutividadeResponse>> produtividadeInspetores() {
        return ResponseEntity.ok(operacionalService.listarProdutividadeInspetores());
    }

    @GetMapping("/atividades-recentes")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atividades recentes de auditoria (ADMIN)")
    public ResponseEntity<List<AuditoriaResponse>> atividadesRecentes(
            @RequestParam(defaultValue = "20") int limite) {
        return ResponseEntity.ok(operacionalService.listarAtividadesRecentes(limite));
    }
}
