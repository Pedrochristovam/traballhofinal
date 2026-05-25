package br.com.sigevi.service;

import br.com.sigevi.dto.response.FotoResponse;
import br.com.sigevi.exception.ResourceNotFoundException;
import br.com.sigevi.mapper.FotoMapper;
import br.com.sigevi.model.Foto;
import br.com.sigevi.model.enums.AcaoAuditoria;
import br.com.sigevi.repository.FotoRepository;
import br.com.sigevi.validator.ImagemValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FotoService {

    private final FotoRepository fotoRepository;
    private final VistoriaService vistoriaService;
    private final FotoMapper fotoMapper;
    private final ImagemValidator imagemValidator;
    private final AuditoriaService auditoriaService;
    private final Path uploadDir;

    public FotoService(FotoRepository fotoRepository,
                       VistoriaService vistoriaService,
                       FotoMapper fotoMapper,
                       ImagemValidator imagemValidator,
                       AuditoriaService auditoriaService,
                       @Value("${sigevi.upload.directory}") String uploadDirectory) throws IOException {
        this.fotoRepository = fotoRepository;
        this.vistoriaService = vistoriaService;
        this.fotoMapper = fotoMapper;
        this.imagemValidator = imagemValidator;
        this.auditoriaService = auditoriaService;
        this.uploadDir = Paths.get(uploadDirectory).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
    }

    @Transactional
    public FotoResponse upload(Long vistoriaId, MultipartFile arquivo, Long usuarioId) {
        imagemValidator.validar(arquivo);
        var vistoria = vistoriaService.buscarEntidade(vistoriaId);

        String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
        Path destino = uploadDir.resolve(nomeArquivo);

        try {
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Falha no upload da imagem", e);
        }

        Foto foto = Foto.builder()
                .vistoria(vistoria)
                .nomeArquivo(arquivo.getOriginalFilename())
                .caminho(destino.toString())
                .contentType(arquivo.getContentType())
                .tamanhoBytes(arquivo.getSize())
                .build();

        foto = fotoRepository.save(foto);
        auditoriaService.registrar("Foto", foto.getId(), AcaoAuditoria.CRIACAO,
                null, foto.getNomeArquivo(), usuarioId);
        return fotoMapper.toResponse(foto, "/api");
    }

    @Transactional(readOnly = true)
    public List<FotoResponse> listarPorVistoria(Long vistoriaId) {
        return fotoRepository.findByVistoriaId(vistoriaId).stream()
                .map(f -> fotoMapper.toResponse(f, "/api"))
                .toList();
    }

    @Transactional(readOnly = true)
    public Resource download(Long fotoId) throws IOException {
        Foto foto = fotoRepository.findById(fotoId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto", fotoId));
        Path path = Paths.get(foto.getCaminho());
        Resource resource = new UrlResource(path.toUri());
        if (!resource.exists()) {
            throw new ResourceNotFoundException("Arquivo da foto", fotoId);
        }
        return resource;
    }
}
