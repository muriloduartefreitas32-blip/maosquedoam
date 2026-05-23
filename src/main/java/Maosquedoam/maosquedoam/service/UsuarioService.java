package Maosquedoam.maosquedoam.service;


import Maosquedoam.maosquedoam.dto.LoginDto;
import Maosquedoam.maosquedoam.dto.UsuarioDto;
import Maosquedoam.maosquedoam.entity.Usuario;
import Maosquedoam.maosquedoam.repository.UsuarioRepository;
import Maosquedoam.maosquedoam.security.JwtService;
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

    @Autowired
    private JwtService jwtService;

    public Usuario cadastrar(UsuarioDto dto){

        Usuario usuario = new Usuario();

        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());

        usuario.setSenha(encoder.encode(dto.getSenha()));

        usuario.setTipoUSuario(dto.getTipoUsuario());

        return usuarioRepository.save(usuario);
    }
    public List<Usuario> listaUsauarios(){
        return usuarioRepository.findAll();
    }

    public String login(LoginDto dto){

        Usuario usuario = usuarioRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException(("Usuario não encontrado")));

                boolean senhaCorreta = encoder.matches(dto.getSenha(), usuario.getSenha());

                if(!senhaCorreta){
                    throw new RuntimeException("Senha incorreta");
                }
                return jwtService.generateToken(usuario.getEmail());
    }

}
