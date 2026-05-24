package Maosquedoam.maosquedoam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NovaSenhaDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String codigo;

    @NotBlank
    private String novasenha;


}
