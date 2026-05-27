package br.com.sigevi.validator;

import br.com.sigevi.exception.BusinessException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * Só cuida de foto — SRP na veia.
 * Num deixa subir PDF, .exe nem arquivo gigante que derruba o servidor, sô.
 */
@Component
public class ImagemValidator {

    private static final Set<String> TIPOS_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/webp"
    );
    private static final long TAMANHO_MAXIMO = 10 * 1024 * 1024; // 10 MB, mais que isso já vira sufoco

    public void validar(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new BusinessException("Arquivo de imagem e obrigatorio");
        }
        if (!TIPOS_PERMITIDOS.contains(arquivo.getContentType())) {
            throw new BusinessException("Formato invalido. Permitidos: JPEG, PNG, WEBP");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new BusinessException("Arquivo excede o tamanho maximo de 10MB");
        }
    }
}
