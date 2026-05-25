package br.com.sigevi.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FotoResponse {

    private Long id;
    private Long vistoriaId;
    private String nomeArquivo;
    private String url;
    private String contentType;
    private Long tamanhoBytes;
    private LocalDateTime criadoEm;
}
