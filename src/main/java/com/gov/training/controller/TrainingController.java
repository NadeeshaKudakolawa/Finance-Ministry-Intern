package com.gov.training.controller;

import com.gov.training.entity.Training;
import com.gov.training.repository.TrainingRepository;
import com.gov.training.service.TrainingService;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@CrossOrigin(origins = "*")
public class TrainingController {

    private final TrainingRepository trainingRepository;
    private final TrainingService trainingService;

    public TrainingController(
            TrainingRepository trainingRepository,
            TrainingService trainingService
    ) {
        this.trainingRepository = trainingRepository;
        this.trainingService = trainingService;
    }

    @GetMapping
    public List<Training> getAll() {
        return trainingRepository.findAll();
    }

    @PostMapping
    public Training create(
            @RequestBody Training training
    ) {
        return trainingRepository.save(training);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Training training
    ) {
        try {

            return ResponseEntity.ok(
                    trainingService.updateTraining(id, training)
            );

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {
        trainingRepository.deleteById(id);
    }
}
