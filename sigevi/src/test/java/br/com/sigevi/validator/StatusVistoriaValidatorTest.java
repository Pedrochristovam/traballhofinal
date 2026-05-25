package br.com.sigevi.validator;

import br.com.sigevi.exception.BusinessException;
import br.com.sigevi.model.enums.StatusVistoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusVistoriaValidatorTest {

    private StatusVistoriaValidator validator;

    @BeforeEach
    void setUp() {
        validator = new StatusVistoriaValidator();
    }

    @Test
    void devePermitirTransicaoAgendadaParaEmAndamento() {
        assertDoesNotThrow(() ->
                validator.validarTransicao(StatusVistoria.AGENDADA, StatusVistoria.EM_ANDAMENTO));
    }

    @Test
    void deveRejeitarTransicaoConcluidaParaAgendada() {
        assertThrows(BusinessException.class, () ->
                validator.validarTransicao(StatusVistoria.CONCLUIDA, StatusVistoria.AGENDADA));
    }

    @Test
    void devePermitirReprovadaParaEmAndamento() {
        assertDoesNotThrow(() ->
                validator.validarTransicao(StatusVistoria.REPROVADA, StatusVistoria.EM_ANDAMENTO));
    }
}
