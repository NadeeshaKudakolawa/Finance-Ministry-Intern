package com.gov.training.service;

import com.gov.training.entity.Training;
import com.gov.training.repository.TrainingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final NominationService nominationService;

    public TrainingService(
            TrainingRepository trainingRepository,
            NominationService nominationService
    ) {
        this.trainingRepository = trainingRepository;
        this.nominationService = nominationService;
    }

    @Transactional
    public Training updateTraining(Long id, Training updated) {

        // Lock the row so this can't race a nomination/cancellation that's
        // reading the same training's capacity.
        Training training = trainingRepository
                .findByIdForUpdate(id)
                .orElseThrow(() ->
                        new RuntimeException("Training not found")
                );

        training.setTitle(updated.getTitle());
        training.setTrainingDate(updated.getTrainingDate());
        training.setVenue(updated.getVenue());
        training.setTrainer(updated.getTrainer());
        training.setMaxParticipants(updated.getMaxParticipants());

        Training saved = trainingRepository.save(training);

        // If capacity went up, pull as many people off the waiting list as
        // there are now free seats.
        nominationService.promoteWaitingList(saved);

        return saved;
    }
}
