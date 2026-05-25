package br.com.sigevi.service;

import br.com.sigevi.dto.request.ImovelRequest;
import br.com.sigevi.exception.BusinessException;
import br.com.sigevi.mapper.ImovelMapper;
import br.com.sigevi.model.Imovel;
import br.com.sigevi.model.enums.TipoImovel;
import br.com.sigevi.repository.ImovelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImovelServiceTest {

    @Mock private ImovelRepository imovelRepository;
    @Mock private ImovelMapper imovelMapper;
    @Mock private AuditoriaService auditoriaService;
    @InjectMocks private ImovelService imovelService;

    @Test
    void deveRejeitarMatriculaDuplicada() {
        ImovelRequest request = new ImovelRequest();
        request.setMatricula("MAT-001");

        when(imovelRepository.existsByMatricula("MAT-001")).thenReturn(true);

        assertThrows(BusinessException.class,
                () -> imovelService.cadastrar(request, 1L));
    }

    @Test
    void deveCadastrarImovel() {
        ImovelRequest request = new ImovelRequest();
        request.setMatricula("MAT-002");
        request.setEndereco("Rua A");
        request.setCidade("SP");
        request.setEstado("SP");
        request.setCep("01000-000");
        request.setTipo(TipoImovel.RESIDENCIAL);

        Imovel imovel = Imovel.builder().id(10L).matricula("MAT-002").build();

        when(imovelRepository.existsByMatricula("MAT-002")).thenReturn(false);
        when(imovelMapper.toEntity(request)).thenReturn(imovel);
        when(imovelRepository.save(imovel)).thenReturn(imovel);
        when(imovelMapper.toResponse(imovel)).thenReturn(
                br.com.sigevi.dto.response.ImovelResponse.builder().id(10L).build());

        imovelService.cadastrar(request, 1L);

        verify(imovelRepository).save(imovel);
    }
}
