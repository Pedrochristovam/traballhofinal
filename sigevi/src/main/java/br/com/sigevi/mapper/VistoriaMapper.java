package br.com.sigevi.mapper;

import br.com.sigevi.dto.response.VistoriaResponse;
import br.com.sigevi.model.Vistoria;
import org.springframework.stereotype.Component;

@Component
public class VistoriaMapper {

    public VistoriaResponse toResponse(Vistoria vistoria) {
        return VistoriaResponse.builder()
                .id(vistoria.getId())
                .imovelId(vistoria.getImovel().getId())
                .imovelMatricula(vistoria.getImovel().getMatricula())
                .inspetorId(vistoria.getInspetor().getId())
                .inspetorNome(vistoria.getInspetor().getNome())
                .status(vistoria.getStatus())
                .dataVistoria(vistoria.getDataVistoria())
                .observacoes(vistoria.getObservacoes())
                .criadoEm(vistoria.getCriadoEm())
                .build();
    }
}
