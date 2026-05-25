package br.com.sigevi.service;

import br.com.sigevi.dto.request.RelatorioRequest;
import br.com.sigevi.dto.response.RelatorioResponse;
import br.com.sigevi.mapper.RelatorioMapper;
import br.com.sigevi.model.Relatorio;
import br.com.sigevi.model.enums.AcaoAuditoria;
import br.com.sigevi.pattern.factory.RelatorioFactory;
import br.com.sigevi.pattern.strategy.RelatorioStrategyContext;
import br.com.sigevi.repository.RelatorioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class RelatorioService {

    private final RelatorioRepository relatorioRepository;
    private final VistoriaService vistoriaService;
    private final UsuarioService usuarioService;
    private final RelatorioStrategyContext strategyContext;
    private final AuditoriaService auditoriaService;
    private final Path relatorioDir;

    public RelatorioService(RelatorioRepository relatorioRepository,
                            VistoriaService vistoriaService,
                            UsuarioService usuarioService,
                            RelatorioStrategyContext strategyContext,
                            AuditoriaService auditoriaService,
                            @Value("${sigevi.upload.directory}") String uploadDirectory) throws IOException {
        this.relatorioRepository = relatorioRepository;
        this.vistoriaService = vistoriaService;
        this.usuarioService = usuarioService;
        this.strategyContext = strategyContext;
        this.auditoriaService = auditoriaService;
        this.relatorioDir = Paths.get(uploadDirectory, "relatorios").toAbsolutePath().normalize();
        Files.createDirectories(this.relatorioDir);
    }

    @Transactional
    public RelatorioResponse gerar(Long vistoriaId, RelatorioRequest request, Long usuarioId) {
        var vistoria = vistoriaService.buscarEntidade(vistoriaId);
        var usuario = usuarioService.buscarEntidade(usuarioId);

        Path arquivoGerado = strategyContext.gerar(request.getTipo(), vistoria, relatorioDir);

        Relatorio relatorio = RelatorioFactory.criar(
                vistoria, request.getTipo(), arquivoGerado.toString(), usuario);
        relatorio = relatorioRepository.save(relatorio);

        auditoriaService.registrar("Relatorio", relatorio.getId(), AcaoAuditoria.CRIACAO,
                null, request.getTipo().name(), usuarioId);

        return RelatorioMapper.toResponse(relatorio);
    }

    @Transactional(readOnly = true)
    public List<RelatorioResponse> listarPorVistoria(Long vistoriaId) {
        return relatorioRepository.findByVistoriaIdOrderByCriadoEmDesc(vistoriaId).stream()
                .map(RelatorioMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Resource download(Long relatorioId) throws IOException {
        Relatorio relatorio = relatorioRepository.findById(relatorioId)
                .orElseThrow(() -> new br.com.sigevi.exception.ResourceNotFoundException("Relatorio", relatorioId));
        return new UrlResource(Paths.get(relatorio.getCaminhoArquivo()).toUri());
    }
}
