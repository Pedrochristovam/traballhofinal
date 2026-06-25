package br.com.sigevi.repository;

import br.com.sigevi.model.Vistoria;
import br.com.sigevi.model.enums.StatusVistoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VistoriaRepository extends JpaRepository<Vistoria, Long> {

    List<Vistoria> findByImovelId(Long imovelId);

    List<Vistoria> findByStatus(StatusVistoria status);

    @Query("SELECT v FROM Vistoria v JOIN FETCH v.imovel JOIN FETCH v.inspetor WHERE v.id = :id")
    Optional<Vistoria> findByIdWithDetails(Long id);

    @Query("SELECT v.status, COUNT(v) FROM Vistoria v GROUP BY v.status")
    List<Object[]> countGroupByStatus();

    @Query("""
            SELECT COUNT(v) FROM Vistoria v
            WHERE v.status IN :statusAtivos
            AND v.dataVistoria < :referencia
            """)
    long countAtrasadas(@Param("referencia") LocalDate referencia,
                        @Param("statusAtivos") List<StatusVistoria> statusAtivos);

    @Query("""
            SELECT v FROM Vistoria v
            JOIN FETCH v.imovel JOIN FETCH v.inspetor
            WHERE v.dataVistoria < :referencia
            AND v.status IN :statusAtivos
            ORDER BY v.dataVistoria ASC
            """)
    List<Vistoria> findAtrasadas(@Param("referencia") LocalDate referencia,
                                 @Param("statusAtivos") List<StatusVistoria> statusAtivos);

    @Query("""
            SELECT v FROM Vistoria v
            JOIN FETCH v.imovel JOIN FETCH v.inspetor
            WHERE (:status IS NULL OR v.status = :status)
            AND (:inspetorId IS NULL OR v.inspetor.id = :inspetorId)
            AND (:dataInicio IS NULL OR v.dataVistoria >= :dataInicio)
            AND (:dataFim IS NULL OR v.dataVistoria <= :dataFim)
            ORDER BY v.dataVistoria DESC, v.id DESC
            """)
    List<Vistoria> findWithFilters(@Param("status") StatusVistoria status,
                                   @Param("inspetorId") Long inspetorId,
                                   @Param("dataInicio") LocalDate dataInicio,
                                   @Param("dataFim") LocalDate dataFim);

    @Query("""
            SELECT COUNT(v) FROM Vistoria v
            WHERE v.status = :status
            AND v.atualizadoEm >= :inicio
            AND v.atualizadoEm < :fim
            """)
    long countByStatusAndAtualizadoEmBetween(@Param("status") StatusVistoria status,
                                             @Param("inicio") LocalDateTime inicio,
                                             @Param("fim") LocalDateTime fim);

    @Query("""
            SELECT v.inspetor.id, v.inspetor.nome,
                   SUM(CASE WHEN v.status = br.com.sigevi.model.enums.StatusVistoria.CONCLUIDA THEN 1 ELSE 0 END),
                   SUM(CASE WHEN v.status IN (
                       br.com.sigevi.model.enums.StatusVistoria.AGENDADA,
                       br.com.sigevi.model.enums.StatusVistoria.EM_ANDAMENTO
                   ) THEN 1 ELSE 0 END),
                   COUNT(v)
            FROM Vistoria v
            GROUP BY v.inspetor.id, v.inspetor.nome
            ORDER BY SUM(CASE WHEN v.status = br.com.sigevi.model.enums.StatusVistoria.CONCLUIDA THEN 1 ELSE 0 END) DESC
            """)
    List<Object[]> findProdutividadeInspetores();

    @Query("""
            SELECT v FROM Vistoria v
            JOIN FETCH v.imovel JOIN FETCH v.inspetor
            WHERE v.inspetor.id = :inspetorId
            AND v.status IN :statusAtivos
            ORDER BY v.dataVistoria ASC, v.id ASC
            """)
    List<Vistoria> findAtivasByInspetor(@Param("inspetorId") Long inspetorId,
                                        @Param("statusAtivos") List<StatusVistoria> statusAtivos);

    long countByInspetorIdAndStatus(Long inspetorId, StatusVistoria status);
}
