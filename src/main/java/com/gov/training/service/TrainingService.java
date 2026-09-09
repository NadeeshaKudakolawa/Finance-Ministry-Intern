package com.gov.training.service;

import com.gov.training.eligibility.EligibilityEvaluator;
import com.gov.training.entity.Officer;
import com.gov.training.entity.Training;
import com.gov.training.repository.OfficerRepository;
import com.gov.training.repository.TrainingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;
    private final NominationService nominationService;
    private final OfficerRepository officerRepository;
    private final EligibilityEvaluator eligibilityEvaluator;

    public TrainingService(
            TrainingRepository trainingRepository,
            NominationService nominationService,
            OfficerRepository officerRepository,
            EligibilityEvaluator eligibilityEvaluator
    ) {
        this.trainingRepository = trainingRepository;
        this.nominationService = nominationService;
        this.officerRepository = officerRepository;
        this.eligibilityEvaluator = eligibilityEvaluator;
    }

    // Officers eligible for a training's programme, per the active
    // EligibilityRuleConfig rows for its programmeCode. A training with no
    // programmeCode has no restrictions, so every officer is eligible.
    public List<Officer> getEligibleOfficers(Long trainingId) {

        Training training = trainingRepository
                .findById(trainingId)
                .orElseThrow(() ->
                        new RuntimeException("Training not found")
                );

        return officerRepository.findAll().stream()
                .filter(officer -> eligibilityEvaluator.evaluate(officer, training).eligible())
                .toList();
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
        training.setProgrammeCode(updated.getProgrammeCode());
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
