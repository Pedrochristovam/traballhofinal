package br.com.sigevi.controller;

import br.com.sigevi.dto.response.AuditoriaResponse;
import br.com.sigevi.service.AuditoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditorias")
@Tag(name = "Auditoria")
@SecurityRequirement(name = "bearerAuth")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping("/{entidade}/{entidadeId}")
    @Operation(summary = "Listar historico de alteracoes de uma entidade")
    public ResponseEntity<List<AuditoriaResponse>> historico(@PathVariable String entidade,
                                                             @PathVariable Long entidadeId) {
        return ResponseEntity.ok(auditoriaService.listarHistorico(entidade, entidadeId));
    }
}
