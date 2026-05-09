package Maosquedoam.maosquedoam.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
@Entity

public class Usuario {
    @Getter
    public Object setsenha;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nome;

    @Setter
    @Getter
    @Email
    @NotBlank
    @Column(unique = true)
    private String email;


    @NotBlank
    private String senha;

   @Setter
   @Getter
   @Enumerated(EnumType.STRING)
    private TipoUsuario tipoUSuario;

    private String telefone;


    private String cidade;


    private String estado;


    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist(){
        this.dataCriacao = LocalDateTime.now();
    }


    public void setnome(String nome) {
    }

    public void setemail(String email) {
    }


    public void setsenha(@Nullable String encode) {
    }
}
