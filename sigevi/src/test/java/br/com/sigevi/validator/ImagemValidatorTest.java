package br.com.sigevi.validator;

import br.com.sigevi.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImagemValidatorTest {

    private ImagemValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ImagemValidator();
    }

    @Test
    void deveAceitarImagemJpeg() {
        MockMultipartFile file = new MockMultipartFile("arquivo", "foto.jpg",
                "image/jpeg", new byte[]{1, 2, 3});
        assertDoesNotThrow(() -> validator.validar(file));
    }

    @Test
    void deveRejeitarFormatoInvalido() {
        MockMultipartFile file = new MockMultipartFile("arquivo", "doc.pdf",
                "application/pdf", new byte[]{1});
        assertThrows(BusinessException.class, () -> validator.validar(file));
    }

    @Test
    void deveRejeitarArquivoVazio() {
        MockMultipartFile file = new MockMultipartFile("arquivo", "",
                "image/jpeg", new byte[0]);
        assertThrows(BusinessException.class, () -> validator.validar(file));
    }
}
