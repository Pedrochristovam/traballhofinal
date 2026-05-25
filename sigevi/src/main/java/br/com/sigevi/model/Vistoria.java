package br.com.sigevi.model;

import br.com.sigevi.model.enums.StatusVistoria;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vistorias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vistoria extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "imovel_id", nullable = false)
    private Imovel imovel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inspetor_id", nullable = false)
    private Usuario inspetor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusVistoria status;

    @Column(name = "data_vistoria", nullable = false)
    private LocalDate dataVistoria;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "vistoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Foto> fotos = new ArrayList<>();

    @OneToMany(mappedBy = "vistoria", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Relatorio> relatorios = new ArrayList<>();
}
