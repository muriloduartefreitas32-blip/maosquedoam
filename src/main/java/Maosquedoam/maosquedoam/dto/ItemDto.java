package Maosquedoam.maosquedoam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDto {
    @NotBlank
    private String titulo;

    @NotBlank
    private String descricao;

    @NotBlank
    private String categoria;

    @NotBlank
    private String estado;


    private Integer quantidade;

    private String localizacao;

    private String imagemUrl;
}
