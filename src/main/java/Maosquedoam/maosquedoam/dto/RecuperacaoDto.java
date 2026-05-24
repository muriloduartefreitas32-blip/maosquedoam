package Maosquedoam.maosquedoam.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecuperacaoDto {

    @NotBlank
    @Email
    private String email;
}
