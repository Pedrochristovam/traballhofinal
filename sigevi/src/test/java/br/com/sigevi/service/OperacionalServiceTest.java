package br.com.sigevi.service;

import br.com.sigevi.dto.response.ResumoOperacionalResponse;
import br.com.sigevi.exception.BusinessException;
import br.com.sigevi.mapper.AuditoriaMapper;
import br.com.sigevi.mapper.OperacionalMapper;
import br.com.sigevi.model.Imovel;
import br.com.sigevi.model.Usuario;
import br.com.sigevi.model.Vistoria;
import br.com.sigevi.model.enums.StatusVistoria;
import br.com.sigevi.repository.AuditoriaRepository;
import br.com.sigevi.repository.ImovelRepository;
import br.com.sigevi.repository.VistoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperacionalServiceTest {

    @Mock private VistoriaRepository vistoriaRepository;
    @Mock private ImovelRepository imovelRepository;
    @Mock private AuditoriaRepository auditoriaRepository;
    @Mock private OperacionalMapper operacionalMapper;
    @Mock private AuditoriaMapper auditoriaMapper;
    @InjectMocks private OperacionalService operacionalService;

    @Test
    void deveMontarResumoOperacional() {
        when(vistoriaRepository.count()).thenReturn(10L);
        when(imovelRepository.count()).thenReturn(4L);
        when(vistoriaRepository.countGroupByStatus()).thenReturn(List.of(
                new Object[]{StatusVistoria.AGENDADA, 3L},
                new Object[]{StatusVistoria.EM_ANDAMENTO, 2L},
                new Object[]{StatusVistoria.CONCLUIDA, 5L}
        ));
        when(vistoriaRepository.countAtrasadas(any(LocalDate.class), eq(List.of(
                StatusVistoria.AGENDADA, StatusVistoria.EM_ANDAMENTO)))).thenReturn(1L);
        when(vistoriaRepository.countByStatusAndAtualizadoEmBetween(
                eq(StatusVistoria.CONCLUIDA), any(), any())).thenReturn(2L, 4L);

        ResumoOperacionalResponse resumo = operacionalService.obterResumo();

        assertEquals(10L, resumo.getTotalVistorias());
        assertEquals(4L, resumo.getTotalImoveis());
        assertEquals(5L, resumo.getVistoriasAtivas());
        assertEquals(1L, resumo.getVistoriasAtrasadas());
        assertEquals(2L, resumo.getConcluidasSemana());
        assertEquals(4L, resumo.getConcluidasMes());
        assertEquals(3L, resumo.getTotalPorStatus().get("AGENDADA"));
    }

    @Test
    void deveRejeitarPeriodoInvalido() {
        LocalDate inicio = LocalDate.of(2026, 6, 10);
        LocalDate fim = LocalDate.of(2026, 6, 1);

        assertThrows(BusinessException.class,
                () -> operacionalService.listarVistorias(null, null, inicio, fim));
    }

    @Test
    void deveRejeitarLimiteInvalido() {
        assertThrows(BusinessException.class,
                () -> operacionalService.listarAtividadesRecentes(0));
        assertThrows(BusinessException.class,
                () -> operacionalService.listarAtividadesRecentes(101));
    }

    @Test
    void deveCalcularMinhaCarga() {
        Vistoria vistoria = Vistoria.builder()
                .id(1L)
                .status(StatusVistoria.AGENDADA)
                .dataVistoria(LocalDate.now().minusDays(2))
                .imovel(Imovel.builder().id(1L).matricula("M1").endereco("Rua A").build())
                .inspetor(Usuario.builder().id(2L).nome("Inspetor").build())
                .build();

        when(vistoriaRepository.findAtivasByInspetor(2L, List.of(
                StatusVistoria.AGENDADA, StatusVistoria.EM_ANDAMENTO)))
                .thenReturn(List.of(vistoria));
        when(vistoriaRepository.countByInspetorIdAndStatus(2L, StatusVistoria.AGENDADA)).thenReturn(1L);
        when(vistoriaRepository.countByInspetorIdAndStatus(2L, StatusVistoria.EM_ANDAMENTO)).thenReturn(0L);
        when(vistoriaRepository.countByInspetorIdAndStatus(2L, StatusVistoria.CONCLUIDA)).thenReturn(3L);
        when(operacionalMapper.isAtrasada(eq(vistoria), any(LocalDate.class))).thenReturn(true);
        when(operacionalMapper.toOperacionalResponse(eq(vistoria), any(LocalDate.class)))
                .thenReturn(br.com.sigevi.dto.response.VistoriaOperacionalResponse.builder()
                        .id(1L)
                        .atrasada(true)
                        .build());

        var carga = operacionalService.obterMinhaCarga(2L);

        assertEquals(1L, carga.getAgendadas());
        assertEquals(1L, carga.getAtrasadas());
        assertEquals(1, carga.getProximasVistorias().size());
    }
}
