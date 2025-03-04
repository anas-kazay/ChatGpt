package anas.kazay.chatapp.repository;

import anas.kazay.chatapp.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ModelRepository extends JpaRepository<Model, Long> {
    Optional<Model> findByName(String name);
}