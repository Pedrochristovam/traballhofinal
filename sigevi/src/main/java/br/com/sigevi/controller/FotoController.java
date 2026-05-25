package br.com.sigevi.controller;

import br.com.sigevi.dto.response.FotoResponse;
import br.com.sigevi.model.Foto;
import br.com.sigevi.repository.FotoRepository;
import br.com.sigevi.security.SecurityUtils;
import br.com.sigevi.service.FotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/fotos")
@Tag(name = "Fotos")
@SecurityRequirement(name = "bearerAuth")
public class FotoController {

    private final FotoService fotoService;
    private final FotoRepository fotoRepository;
    private final SecurityUtils securityUtils;

    public FotoController(FotoService fotoService, FotoRepository fotoRepository, SecurityUtils securityUtils) {
        this.fotoService = fotoService;
        this.fotoRepository = fotoRepository;
        this.securityUtils = securityUtils;
    }

    @PostMapping(value = "/vistoria/{vistoriaId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de foto para vistoria")
    public ResponseEntity<FotoResponse> upload(@PathVariable Long vistoriaId,
                                               @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(fotoService.upload(vistoriaId, arquivo, securityUtils.getUsuarioLogadoId()));
    }

    @GetMapping("/vistoria/{vistoriaId}")
    @Operation(summary = "Listar fotos da vistoria")
    public ResponseEntity<List<FotoResponse>> listar(@PathVariable Long vistoriaId) {
        return ResponseEntity.ok(fotoService.listarPorVistoria(vistoriaId));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download da foto")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        Resource resource = fotoService.download(id);
        Foto foto = fotoRepository.findById(id).orElseThrow();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(foto.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + foto.getNomeArquivo() + "\"")
                .body(resource);
    }
}
