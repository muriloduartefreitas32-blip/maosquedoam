package Maosquedoam.maosquedoam.dto;

import Maosquedoam.maosquedoam.entity.TipoUsuario;
import lombok.Getter;
import lombok.Setter;

public class UsuarioDto {

    @Setter
    @Getter
    private String nome;
    @Setter
    @Getter
    private String email;
    @Getter
    @Setter
    private String senha;
    private TipoUsuario tipoUsuario;

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }
}
