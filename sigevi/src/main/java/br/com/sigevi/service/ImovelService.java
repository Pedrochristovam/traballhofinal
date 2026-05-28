package br.com.sigevi.service;

import br.com.sigevi.dto.request.ImovelRequest;
import br.com.sigevi.dto.response.ImovelResponse;
import br.com.sigevi.exception.BusinessException;
import br.com.sigevi.exception.ResourceNotFoundException;
import br.com.sigevi.mapper.ImovelMapper;
import br.com.sigevi.model.Imovel;
import br.com.sigevi.model.enums.AcaoAuditoria;
import br.com.sigevi.repository.ImovelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Cadastro e busca de imóvel. */
@Service
public class ImovelService {

    private final ImovelRepository imovelRepository;
    private final ImovelMapper imovelMapper;
    private final AuditoriaService auditoriaService;

    public ImovelService(ImovelRepository imovelRepository, ImovelMapper imovelMapper,
                         AuditoriaService auditoriaService) {
        this.imovelRepository = imovelRepository;
        this.imovelMapper = imovelMapper;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public ImovelResponse cadastrar(ImovelRequest request, Long usuarioId) {
        if (imovelRepository.existsByMatricula(request.getMatricula())) {
            throw new BusinessException("Matricula ja cadastrada");
        }
        Imovel imovel = imovelRepository.save(imovelMapper.toEntity(request));
        auditoriaService.registrar("Imovel", imovel.getId(), AcaoAuditoria.CRIACAO,
                null, imovel.getMatricula(), usuarioId);
        return imovelMapper.toResponse(imovel);
    }

    @Transactional(readOnly = true)
    public ImovelResponse buscarPorId(Long id) {
        return imovelMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ImovelResponse atualizar(Long id, ImovelRequest request, Long usuarioId) {
        Imovel imovel = buscarEntidade(id);
        String matriculaAnterior = imovel.getMatricula();
        imovelMapper.updateEntity(imovel, request);
        imovel = imovelRepository.save(imovel);
        auditoriaService.registrar("Imovel", id, AcaoAuditoria.ATUALIZACAO,
                matriculaAnterior, imovel.getMatricula(), usuarioId);
        return imovelMapper.toResponse(imovel);
    }

    @Transactional(readOnly = true)
    public ImovelResponse buscarPorMatricula(String matricula) {
        Imovel imovel = imovelRepository.findByMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("Imovel com matricula: " + matricula));
        return imovelMapper.toResponse(imovel);
    }

    @Transactional(readOnly = true)
    public List<ImovelResponse> buscarPorEndereco(String endereco) {
        return imovelRepository.findByEnderecoContainingIgnoreCase(endereco).stream()
                .map(imovelMapper::toResponse)
                .toList();
    }

    public Imovel buscarEntidade(Long id) {
        return imovelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Imovel", id));
    }
}
