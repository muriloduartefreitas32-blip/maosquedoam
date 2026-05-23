package Maosquedoam.maosquedoam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SolicitacaoDto {

    @NotNull
    private Long itemId;

    private String mensagem;

}
