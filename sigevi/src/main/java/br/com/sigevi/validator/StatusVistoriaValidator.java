package br.com.sigevi.validator;

import br.com.sigevi.exception.BusinessException;
import br.com.sigevi.model.enums.StatusVistoria;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/** Valida mudança de status da vistoria — num deixa pular etapa. */
@Component
public class StatusVistoriaValidator {

    private static final Map<StatusVistoria, Set<StatusVistoria>> TRANSICOES = new EnumMap<>(StatusVistoria.class);

    static {
        TRANSICOES.put(StatusVistoria.AGENDADA, Set.of(StatusVistoria.EM_ANDAMENTO, StatusVistoria.CANCELADA));
        TRANSICOES.put(StatusVistoria.EM_ANDAMENTO, Set.of(StatusVistoria.CONCLUIDA, StatusVistoria.REPROVADA, StatusVistoria.CANCELADA));
        TRANSICOES.put(StatusVistoria.CONCLUIDA, Set.of());
        TRANSICOES.put(StatusVistoria.REPROVADA, Set.of(StatusVistoria.EM_ANDAMENTO));
        TRANSICOES.put(StatusVistoria.CANCELADA, Set.of());
    }

    public void validarTransicao(StatusVistoria atual, StatusVistoria novo) {
        Set<StatusVistoria> permitidos = TRANSICOES.getOrDefault(atual, Set.of());
        if (!permitidos.contains(novo)) {
            throw new BusinessException(
                    String.format("Transicao invalida de %s para %s", atual, novo));
        }
    }
}
