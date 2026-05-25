package br.com.sigevi.repository;

import br.com.sigevi.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByEntidadeAndEntidadeIdOrderByCriadoEmDesc(String entidade, Long entidadeId);
}
