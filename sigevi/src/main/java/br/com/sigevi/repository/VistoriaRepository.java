package br.com.sigevi.repository;

import br.com.sigevi.model.Vistoria;
import br.com.sigevi.model.enums.StatusVistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VistoriaRepository extends JpaRepository<Vistoria, Long> {

    List<Vistoria> findByImovelId(Long imovelId);

    List<Vistoria> findByStatus(StatusVistoria status);

    @Query("SELECT v FROM Vistoria v JOIN FETCH v.imovel JOIN FETCH v.inspetor WHERE v.id = :id")
    java.util.Optional<Vistoria> findByIdWithDetails(Long id);
}
