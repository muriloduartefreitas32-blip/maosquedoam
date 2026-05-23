package Maosquedoam.maosquedoam.repository;

import Maosquedoam.maosquedoam.entity.Item;
import Maosquedoam.maosquedoam.entity.StatusItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepostitory extends JpaRepository<Item, Long> {

    List<Item> findByCategoria(String categoria);

    List<Item> findByLocalizacaoContainingIgnoreCase(String localizacao);

    List<Item> findByTituloContainingIgnoreCaseOrDescricaoContainingIgnoreCase(String titulo, String desricao);

    List<Item> findByDoadorId(Long doadorId);

    List<Item> findByStatus(StatusItem status);
}
