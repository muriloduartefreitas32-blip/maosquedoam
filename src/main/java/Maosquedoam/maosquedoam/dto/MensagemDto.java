package Maosquedoam.maosquedoam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MensagemDto {

    @NotNull
    private Long solicitacaoId;

    @NotNull
    private Long destinatario;

    @NotBlank
    private String conteudo;
}
