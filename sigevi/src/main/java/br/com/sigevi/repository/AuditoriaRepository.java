package br.com.sigevi.repository;

import br.com.sigevi.model.Auditoria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByEntidadeAndEntidadeIdOrderByCriadoEmDesc(String entidade, Long entidadeId);

    @Query("""
            SELECT a FROM Auditoria a
            LEFT JOIN FETCH a.usuario
            ORDER BY a.criadoEm DESC
            """)
    List<Auditoria> findRecentes(Pageable pageable);
}
