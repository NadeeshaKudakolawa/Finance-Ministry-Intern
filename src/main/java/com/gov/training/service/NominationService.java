package com.gov.training.service;

import com.gov.training.dto.NominationRequest;
import com.gov.training.entity.*;
import com.gov.training.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NominationService {

    private final NominationRepository nominationRepository;
    private final OfficerRepository officerRepository;
    private final TrainingRepository trainingRepository;
    private final DepartmentRepository departmentRepository;

    public NominationService(
            NominationRepository nominationRepository,
            OfficerRepository officerRepository,
            TrainingRepository trainingRepository,
            DepartmentRepository departmentRepository
    ) {
        this.nominationRepository = nominationRepository;
        this.officerRepository = officerRepository;
        this.trainingRepository = trainingRepository;
        this.departmentRepository = departmentRepository;
    }

    public Nomination createNomination(
            NominationRequest request
    ) {

        Training training = trainingRepository
                .findById(request.getTrainingId())
                .orElseThrow(() ->
                        new RuntimeException("Training not found")
                );

        Officer officer = officerRepository
                .findById(request.getOfficerId())
                .orElseThrow(() ->
                        new RuntimeException("Officer not found")
                );

        Department department = departmentRepository
                .findById(request.getDepartmentId())
                .orElseThrow(() ->
                        new RuntimeException("Department not found")
                );

        boolean duplicate =
                nominationRepository
                    .existsByTrainingIdAndOfficerId(
                        training.getId(),
                        officer.getId()
                    );

        if (duplicate) {
            throw new RuntimeException(
                "Duplicate nomination. Officer is already nominated for this training."
            );
        }

        long currentParticipants =
                nominationRepository
                    .countByTrainingId(training.getId());

        if (currentParticipants >=
                training.getMaxParticipants()) {

            throw new RuntimeException(
                "Training is full. Maximum participant limit reached."
            );
        }

        Nomination nomination = new Nomination();

        nomination.setTraining(training);
        nomination.setOfficer(officer);
        nomination.setNominatedByDepartment(department);
        nomination.setNominationDate(
                LocalDateTime.now()
        );

        return nominationRepository.save(nomination);
    }
}
