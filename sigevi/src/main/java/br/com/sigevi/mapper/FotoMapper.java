package br.com.sigevi.mapper;

import br.com.sigevi.dto.response.FotoResponse;
import br.com.sigevi.model.Foto;
import org.springframework.stereotype.Component;

@Component
public class FotoMapper {

    public FotoResponse toResponse(Foto foto, String baseUrl) {
        return FotoResponse.builder()
                .id(foto.getId())
                .vistoriaId(foto.getVistoria().getId())
                .nomeArquivo(foto.getNomeArquivo())
                .url(baseUrl + "/fotos/" + foto.getId() + "/download")
                .contentType(foto.getContentType())
                .tamanhoBytes(foto.getTamanhoBytes())
                .criadoEm(foto.getCriadoEm())
                .build();
    }
}
