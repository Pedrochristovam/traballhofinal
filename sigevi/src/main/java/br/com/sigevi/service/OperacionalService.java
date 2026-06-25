package br.com.sigevi.service;

import br.com.sigevi.dto.response.AuditoriaResponse;
import br.com.sigevi.dto.response.InspetorProdutividadeResponse;
import br.com.sigevi.dto.response.MinhaCargaResponse;
import br.com.sigevi.dto.response.ResumoOperacionalResponse;
import br.com.sigevi.dto.response.VistoriaOperacionalResponse;
import br.com.sigevi.exception.BusinessException;
import br.com.sigevi.mapper.AuditoriaMapper;
import br.com.sigevi.mapper.OperacionalMapper;
import br.com.sigevi.model.enums.StatusVistoria;
import br.com.sigevi.repository.AuditoriaRepository;
import br.com.sigevi.repository.ImovelRepository;
import br.com.sigevi.repository.VistoriaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Consultas agregadas para visao operacional de vistorias. */
@Service
public class OperacionalService {

    private static final List<StatusVistoria> STATUS_ATIVOS = List.of(
            StatusVistoria.AGENDADA,
            StatusVistoria.EM_ANDAMENTO
    );

    private static final int LIMITE_MINIMO = 1;
    private static final int LIMITE_MAXIMO = 100;
    private static final int PROXIMAS_VISTORIAS_LIMITE = 5;

    private final VistoriaRepository vistoriaRepository;
    private final ImovelRepository imovelRepository;
    private final AuditoriaRepository auditoriaRepository;
    private final OperacionalMapper operacionalMapper;
    private final AuditoriaMapper auditoriaMapper;

    public OperacionalService(VistoriaRepository vistoriaRepository,
                              ImovelRepository imovelRepository,
                              AuditoriaRepository auditoriaRepository,
                              OperacionalMapper operacionalMapper,
                              AuditoriaMapper auditoriaMapper) {
        this.vistoriaRepository = vistoriaRepository;
        this.imovelRepository = imovelRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.operacionalMapper = operacionalMapper;
        this.auditoriaMapper = auditoriaMapper;
    }

    @Transactional(readOnly = true)
    public ResumoOperacionalResponse obterResumo() {
        LocalDate hoje = LocalDate.now();
        Map<String, Long> totalPorStatus = montarMapaStatus();
        long vistoriasAtivas = STATUS_ATIVOS.stream()
                .mapToLong(status -> totalPorStatus.getOrDefault(status.name(), 0L))
                .sum();

        LocalDateTime inicioSemana = hoje.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
        LocalDateTime inicioMes = hoje.withDayOfMonth(1).atStartOfDay();
        LocalDateTime inicioProximoDia = hoje.plusDays(1).atStartOfDay();

        return ResumoOperacionalResponse.builder()
                .totalVistorias(vistoriaRepository.count())
                .totalImoveis(imovelRepository.count())
                .vistoriasAtivas(vistoriasAtivas)
                .vistoriasAtrasadas(vistoriaRepository.countAtrasadas(hoje, STATUS_ATIVOS))
                .concluidasSemana(vistoriaRepository.countByStatusAndAtualizadoEmBetween(
                        StatusVistoria.CONCLUIDA, inicioSemana, inicioProximoDia))
                .concluidasMes(vistoriaRepository.countByStatusAndAtualizadoEmBetween(
                        StatusVistoria.CONCLUIDA, inicioMes, inicioProximoDia))
                .totalPorStatus(totalPorStatus)
                .build();
    }

    @Transactional(readOnly = true)
    public MinhaCargaResponse obterMinhaCarga(Long inspetorId) {
        LocalDate hoje = LocalDate.now();
        var vistoriasAtivas = vistoriaRepository.findAtivasByInspetor(inspetorId, STATUS_ATIVOS);

        long totalAtrasadas = vistoriasAtivas.stream()
                .filter(v -> operacionalMapper.isAtrasada(v, hoje))
                .count();

        List<VistoriaOperacionalResponse> proximas = vistoriasAtivas.stream()
                .map(vistoria -> operacionalMapper.toOperacionalResponse(vistoria, hoje))
                .limit(PROXIMAS_VISTORIAS_LIMITE)
                .toList();

        return MinhaCargaResponse.builder()
                .agendadas(vistoriaRepository.countByInspetorIdAndStatus(inspetorId, StatusVistoria.AGENDADA))
                .emAndamento(vistoriaRepository.countByInspetorIdAndStatus(inspetorId, StatusVistoria.EM_ANDAMENTO))
                .atrasadas(totalAtrasadas)
                .concluidas(vistoriaRepository.countByInspetorIdAndStatus(inspetorId, StatusVistoria.CONCLUIDA))
                .proximasVistorias(proximas)
                .build();
    }

    @Transactional(readOnly = true)
    public List<VistoriaOperacionalResponse> listarVistorias(StatusVistoria status,
                                                             Long inspetorId,
                                                             LocalDate dataInicio,
                                                             LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        LocalDate hoje = LocalDate.now();
        return vistoriaRepository.findWithFilters(status, inspetorId, dataInicio, dataFim).stream()
                .map(vistoria -> operacionalMapper.toOperacionalResponse(vistoria, hoje))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VistoriaOperacionalResponse> listarAtrasadas() {
        LocalDate hoje = LocalDate.now();
        return vistoriaRepository.findAtrasadas(hoje, STATUS_ATIVOS).stream()
                .map(vistoria -> operacionalMapper.toOperacionalResponse(vistoria, hoje))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InspetorProdutividadeResponse> listarProdutividadeInspetores() {
        return vistoriaRepository.findProdutividadeInspetores().stream()
                .map(row -> InspetorProdutividadeResponse.builder()
                        .inspetorId((Long) row[0])
                        .inspetorNome((String) row[1])
                        .concluidas(((Number) row[2]).longValue())
                        .pendentes(((Number) row[3]).longValue())
                        .totalVistorias(((Number) row[4]).longValue())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditoriaResponse> listarAtividadesRecentes(int limite) {
        int limiteNormalizado = normalizarLimite(limite);
        return auditoriaRepository.findRecentes(PageRequest.of(0, limiteNormalizado)).stream()
                .map(auditoriaMapper::toResponse)
                .toList();
    }

    private Map<String, Long> montarMapaStatus() {
        Map<String, Long> totalPorStatus = new LinkedHashMap<>();
        for (StatusVistoria status : StatusVistoria.values()) {
            totalPorStatus.put(status.name(), 0L);
        }
        for (Object[] row : vistoriaRepository.countGroupByStatus()) {
            StatusVistoria status = (StatusVistoria) row[0];
            totalPorStatus.put(status.name(), ((Number) row[1]).longValue());
        }
        return totalPorStatus;
    }

    private void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new BusinessException("dataInicio deve ser anterior ou igual a dataFim");
        }
    }

    private int normalizarLimite(int limite) {
        if (limite < LIMITE_MINIMO || limite > LIMITE_MAXIMO) {
            throw new BusinessException("limite deve estar entre " + LIMITE_MINIMO + " e " + LIMITE_MAXIMO);
        }
        return limite;
    }
}
