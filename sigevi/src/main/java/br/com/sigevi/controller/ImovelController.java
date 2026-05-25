package br.com.sigevi.controller;

import br.com.sigevi.dto.request.ImovelRequest;
import br.com.sigevi.dto.response.ImovelResponse;
import br.com.sigevi.security.SecurityUtils;
import br.com.sigevi.service.ImovelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/imoveis")
@Tag(name = "Imoveis")
@SecurityRequirement(name = "bearerAuth")
public class ImovelController {

    private final ImovelService imovelService;
    private final SecurityUtils securityUtils;

    public ImovelController(ImovelService imovelService, SecurityUtils securityUtils) {
        this.imovelService = imovelService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    @Operation(summary = "Cadastrar imovel")
    public ResponseEntity<ImovelResponse> cadastrar(@Valid @RequestBody ImovelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(imovelService.cadastrar(request, securityUtils.getUsuarioLogadoId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar imovel por ID")
    public ResponseEntity<ImovelResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(imovelService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar imovel")
    public ResponseEntity<ImovelResponse> atualizar(@PathVariable Long id,
                                                  @Valid @RequestBody ImovelRequest request) {
        return ResponseEntity.ok(imovelService.atualizar(id, request, securityUtils.getUsuarioLogadoId()));
    }

    @GetMapping("/matricula/{matricula}")
    @Operation(summary = "Buscar imovel por matricula")
    public ResponseEntity<ImovelResponse> buscarPorMatricula(@PathVariable String matricula) {
        return ResponseEntity.ok(imovelService.buscarPorMatricula(matricula));
    }

    @GetMapping("/endereco")
    @Operation(summary = "Buscar imoveis por endereco")
    public ResponseEntity<List<ImovelResponse>> buscarPorEndereco(@RequestParam String q) {
        return ResponseEntity.ok(imovelService.buscarPorEndereco(q));
    }
}
