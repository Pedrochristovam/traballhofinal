package br.com.sigevi.mapper;

import br.com.sigevi.dto.request.ImovelRequest;
import br.com.sigevi.dto.response.ImovelResponse;
import br.com.sigevi.model.Imovel;
import org.springframework.stereotype.Component;

@Component
public class ImovelMapper {

    public ImovelResponse toResponse(Imovel imovel) {
        return ImovelResponse.builder()
                .id(imovel.getId())
                .matricula(imovel.getMatricula())
                .endereco(imovel.getEndereco())
                .cidade(imovel.getCidade())
                .estado(imovel.getEstado())
                .cep(imovel.getCep())
                .tipo(imovel.getTipo())
                .areaM2(imovel.getAreaM2())
                .descricao(imovel.getDescricao())
                .criadoEm(imovel.getCriadoEm())
                .build();
    }

    public Imovel toEntity(ImovelRequest request) {
        return Imovel.builder()
                .matricula(request.getMatricula())
                .endereco(request.getEndereco())
                .cidade(request.getCidade())
                .estado(request.getEstado().toUpperCase())
                .cep(request.getCep())
                .tipo(request.getTipo())
                .areaM2(request.getAreaM2())
                .descricao(request.getDescricao())
                .build();
    }

    public void updateEntity(Imovel imovel, ImovelRequest request) {
        imovel.setMatricula(request.getMatricula());
        imovel.setEndereco(request.getEndereco());
        imovel.setCidade(request.getCidade());
        imovel.setEstado(request.getEstado().toUpperCase());
        imovel.setCep(request.getCep());
        imovel.setTipo(request.getTipo());
        imovel.setAreaM2(request.getAreaM2());
        imovel.setDescricao(request.getDescricao());
    }
}
