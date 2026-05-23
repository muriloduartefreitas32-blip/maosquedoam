package Maosquedoam.maosquedoam.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "itens")
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String descricao;
    private String categoria;
    private String estado;
    private Integer quantidade;
    private String localizacao;
    private String imagemUrl;

    @Enumerated(EnumType.STRING)
    private StatusItem status;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario doador;

    @CreationTimestamp
    private LocalDateTime datacriacao;
}
