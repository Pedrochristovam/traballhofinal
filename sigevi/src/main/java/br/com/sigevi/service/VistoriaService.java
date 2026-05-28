package br.com.sigevi.service;

import br.com.sigevi.dto.request.ObservacaoRequest;
import br.com.sigevi.dto.request.StatusVistoriaRequest;
import br.com.sigevi.dto.request.VistoriaRequest;
import br.com.sigevi.dto.response.VistoriaResponse;
import br.com.sigevi.exception.ResourceNotFoundException;
import br.com.sigevi.mapper.VistoriaMapper;
import br.com.sigevi.model.Vistoria;
import br.com.sigevi.model.enums.AcaoAuditoria;
import br.com.sigevi.model.enums.StatusVistoria;
import br.com.sigevi.repository.VistoriaRepository;
import br.com.sigevi.validator.StatusVistoriaValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Cria vistoria, muda status e observação. */
@Service
public class VistoriaService {

    private final VistoriaRepository vistoriaRepository;
    private final ImovelService imovelService;
    private final UsuarioService usuarioService;
    private final VistoriaMapper vistoriaMapper;
    private final StatusVistoriaValidator statusValidator;
    private final AuditoriaService auditoriaService;

    public VistoriaService(VistoriaRepository vistoriaRepository,
                           ImovelService imovelService,
                           UsuarioService usuarioService,
                           VistoriaMapper vistoriaMapper,
                           StatusVistoriaValidator statusValidator,
                           AuditoriaService auditoriaService) {
        this.vistoriaRepository = vistoriaRepository;
        this.imovelService = imovelService;
        this.usuarioService = usuarioService;
        this.vistoriaMapper = vistoriaMapper;
        this.statusValidator = statusValidator;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public VistoriaResponse criar(VistoriaRequest request, Long usuarioId) {
        Vistoria vistoria = Vistoria.builder()
                .imovel(imovelService.buscarEntidade(request.getImovelId()))
                .inspetor(usuarioService.buscarEntidade(request.getInspetorId()))
                .status(StatusVistoria.AGENDADA)
                .dataVistoria(request.getDataVistoria())
                .observacoes(request.getObservacoes())
                .build();
        vistoria = vistoriaRepository.save(vistoria);
        auditoriaService.registrar("Vistoria", vistoria.getId(), AcaoAuditoria.CRIACAO,
                null, vistoria.getStatus().name(), usuarioId);
        return vistoriaMapper.toResponse(vistoria);
    }

    @Transactional(readOnly = true)
    public VistoriaResponse buscarPorId(Long id) {
        return vistoriaMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public VistoriaResponse alterarStatus(Long id, StatusVistoriaRequest request, Long usuarioId) {
        Vistoria vistoria = buscarEntidade(id);
        StatusVistoria statusAnterior = vistoria.getStatus();
        statusValidator.validarTransicao(statusAnterior, request.getStatus());
        vistoria.setStatus(request.getStatus());
        vistoria = vistoriaRepository.save(vistoria);
        auditoriaService.registrar("Vistoria", id, AcaoAuditoria.STATUS_ALTERADO,
                statusAnterior.name(), request.getStatus().name(), usuarioId);
        return vistoriaMapper.toResponse(vistoria);
    }

    @Transactional
    public VistoriaResponse adicionarObservacoes(Long id, ObservacaoRequest request, Long usuarioId) {
        Vistoria vistoria = buscarEntidade(id);
        String anterior = vistoria.getObservacoes();
        vistoria.setObservacoes(request.getObservacoes());
        vistoria = vistoriaRepository.save(vistoria);
        auditoriaService.registrar("Vistoria", id, AcaoAuditoria.ATUALIZACAO,
                anterior, request.getObservacoes(), usuarioId);
        return vistoriaMapper.toResponse(vistoria);
    }

    @Transactional(readOnly = true)
    public List<VistoriaResponse> listarPorImovel(Long imovelId) {
        return vistoriaRepository.findByImovelId(imovelId).stream()
                .map(vistoriaMapper::toResponse)
                .toList();
    }

    public Vistoria buscarEntidade(Long id) {
        return vistoriaRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vistoria", id));
    }
}
