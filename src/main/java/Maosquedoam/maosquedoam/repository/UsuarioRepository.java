package Maosquedoam.maosquedoam.repository;

import Maosquedoam.maosquedoam.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository  extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    String email(String email);
}
