package Maosquedoam.maosquedoam.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    private String titulo;

    @NotBlank
    private String descricao;

    @NotBlank
    private String categoria;

    @NotBlank
    //condicões do item
    private String estado;

    @NotBlank
    private Integer quantidade;

    @NotBlank
    private String localizacao;

    @NotBlank
    private String imagemUrl;

    @Enumerated(EnumType.STRING)
    private StatusItem status;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario doador;

    @CreationTimestamp
    private LocalDateTime datacriacao;
}
