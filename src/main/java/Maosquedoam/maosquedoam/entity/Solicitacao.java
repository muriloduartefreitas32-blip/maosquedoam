package Maosquedoam.maosquedoam.entity;

import Maosquedoam.maosquedoam.enuns.StatusSolicitacao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacoes")
@Getter
@Setter
public class Solicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "beneficiario_id")
    private Usuario beneficiario;

    @Enumerated(EnumType.STRING)
    private StatusSolicitacao status;

    private String mensagem;

    @CreationTimestamp
    private LocalDateTime dataAbertura;

    private LocalDateTime dataAtualizacao;
}
