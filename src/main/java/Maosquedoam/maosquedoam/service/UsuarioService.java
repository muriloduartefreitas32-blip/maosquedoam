package Maosquedoam.maosquedoam.service;


import Maosquedoam.maosquedoam.dto.UsuarioDto;
import Maosquedoam.maosquedoam.entity.Usuario;
import Maosquedoam.maosquedoam.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder encoder;

    public Usuario cadastrar(UsuarioDto dto){

        Usuario usuario = new Usuario();

        usuario.setnome(dto.getNome());
        usuario.setemail(dto.getEmail());

        usuario.setsenha(encoder.encode(dto.getSenha()));

        usuario.setTipoUSuario(dto.getTipoUsuario());

        return usuarioRepository.save(usuario);
    }
    public List<Usuario> listaUsauarios(){
        return usuarioRepository.findAll();
    }



}
