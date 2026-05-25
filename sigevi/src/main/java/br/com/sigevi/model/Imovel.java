package br.com.sigevi.model;

import br.com.sigevi.model.enums.TipoImovel;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "imoveis")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Imovel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String matricula;

    @Column(nullable = false, length = 300)
    private String endereco;

    @Column(nullable = false, length = 100)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false, length = 10)
    private String cep;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoImovel tipo;

    @Column(name = "area_m2", precision = 10, scale = 2)
    private BigDecimal areaM2;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Vistoria> vistorias = new ArrayList<>();
}
