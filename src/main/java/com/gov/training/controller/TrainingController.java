package com.gov.training.controller;

import com.gov.training.entity.Training;
import com.gov.training.repository.TrainingRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainings")
@CrossOrigin(origins = "*")
public class TrainingController {

    private final TrainingRepository trainingRepository;

    public TrainingController(
            TrainingRepository trainingRepository
    ) {
        this.trainingRepository = trainingRepository;
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
}
