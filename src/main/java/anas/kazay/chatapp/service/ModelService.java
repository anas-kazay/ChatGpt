package anas.kazay.chatapp.service;

import anas.kazay.chatapp.model.Model;
import anas.kazay.chatapp.repository.ModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModelService {

    private final ModelRepository modelRepository;

    public ModelService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    public List<Model> getAllActiveModels() {
        return modelRepository.findAll().stream()
                .filter(Model::isActive)
                .toList();
    }

    public Model getModelById(Long id) {
        return modelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Model not found"));
    }
}
