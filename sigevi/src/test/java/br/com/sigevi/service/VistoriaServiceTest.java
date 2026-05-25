package br.com.sigevi.service;

import br.com.sigevi.dto.request.StatusVistoriaRequest;
import br.com.sigevi.exception.ResourceNotFoundException;
import br.com.sigevi.mapper.VistoriaMapper;
import br.com.sigevi.model.Imovel;
import br.com.sigevi.model.Usuario;
import br.com.sigevi.model.Vistoria;
import br.com.sigevi.model.enums.StatusVistoria;
import br.com.sigevi.repository.VistoriaRepository;
import br.com.sigevi.validator.StatusVistoriaValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VistoriaServiceTest {

    @Mock private VistoriaRepository vistoriaRepository;
    @Mock private ImovelService imovelService;
    @Mock private UsuarioService usuarioService;
    @Mock private VistoriaMapper vistoriaMapper;
    @Mock private StatusVistoriaValidator statusValidator;
    @Mock private AuditoriaService auditoriaService;
    @InjectMocks private VistoriaService vistoriaService;

    @Test
    void deveAlterarStatusComSucesso() {
        Vistoria vistoria = Vistoria.builder()
                .id(1L)
                .status(StatusVistoria.AGENDADA)
                .imovel(Imovel.builder().id(1L).matricula("M1").build())
                .inspetor(Usuario.builder().id(2L).nome("Inspetor").build())
                .build();

        StatusVistoriaRequest request = new StatusVistoriaRequest();
        request.setStatus(StatusVistoria.EM_ANDAMENTO);

        when(vistoriaRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(vistoria));
        when(vistoriaRepository.save(vistoria)).thenReturn(vistoria);

        vistoriaService.alterarStatus(1L, request, 2L);

        verify(statusValidator).validarTransicao(StatusVistoria.AGENDADA, StatusVistoria.EM_ANDAMENTO);
        verify(vistoriaRepository).save(vistoria);
    }

    @Test
    void deveLancarNotFoundQuandoVistoriaInexistente() {
        when(vistoriaRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());
        StatusVistoriaRequest request = new StatusVistoriaRequest();
        request.setStatus(StatusVistoria.EM_ANDAMENTO);

        assertThrows(ResourceNotFoundException.class,
                () -> vistoriaService.alterarStatus(99L, request, 1L));
    }
}
