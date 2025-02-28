package anas.kazay.chatapp.controller;

import anas.kazay.chatapp.model.Model;
import anas.kazay.chatapp.service.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping
    public ResponseEntity<List<Model>> getAllModels() {
        return ResponseEntity.ok(modelService.getAllActiveModels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Model> getModel(@PathVariable Long id) {
        return ResponseEntity.ok(modelService.getModelById(id));
    }
}