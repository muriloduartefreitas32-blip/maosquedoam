package Maosquedoam.maosquedoam.service;


import Maosquedoam.maosquedoam.dto.LoginDto;
import Maosquedoam.maosquedoam.dto.NovaSenhaDto;
import Maosquedoam.maosquedoam.dto.UsuarioDto;
import Maosquedoam.maosquedoam.entity.Usuario;
import Maosquedoam.maosquedoam.repository.UsuarioRepository;
import Maosquedoam.maosquedoam.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private EmaiService emaiService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void solicitarRecuperacao(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        // Gera código de 6 dígitos
        String codigo = String.valueOf((int)(Math.random() * 900000) + 100000);

        usuario.setCodigoRecuperacao(codigo);
        usuario.setExpiracaoCodigoRecuperacao(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        emaiService.enviarEmailRecuperacao(email, codigo);
    }

    public void redefinirSenha(NovaSenhaDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        if (usuario.getCodigoRecuperacao() == null ||
                !usuario.getCodigoRecuperacao().equals(dto.getCodigo())) {
            throw new RuntimeException("Código inválido");
        }

        if (LocalDateTime.now().isAfter(usuario.getExpiracaoCodigoRecuperacao())) {
            throw new RuntimeException("Código expirado");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getNovasenha()));
        usuario.setCodigoRecuperacao(null);
        usuario.setExpiracaoCodigoRecuperacao(null);
        usuarioRepository.save(usuario);
    }

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
