package com.gov.training.service;

import com.gov.training.dto.NominationRequest;
import com.gov.training.entity.*;
import com.gov.training.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public Nomination createNomination(
            NominationRequest request
    ) {

        // Lock the training row first so the capacity check below and any
        // concurrent create/cancel for the same training are serialized.
        Training training = trainingRepository
                .findByIdForUpdate(request.getTrainingId())
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

        Optional<Nomination> existing =
                nominationRepository.findByTrainingIdAndOfficerId(
                        training.getId(),
                        officer.getId()
                );

        if (existing.isPresent()
                && existing.get().getStatus() != NominationStatus.CANCELLED) {
            throw new RuntimeException(
                "Duplicate nomination. Officer is already nominated for this training."
            );
        }

        long confirmedCount =
                nominationRepository.countByTrainingIdAndStatus(
                        training.getId(),
                        NominationStatus.CONFIRMED
                );

        NominationStatus status =
                confirmedCount < training.getMaxParticipants()
                        ? NominationStatus.CONFIRMED
                        : NominationStatus.WAITING;

        // Reuse a previously cancelled row so the (training_id, officer_id)
        // unique constraint still blocks true duplicates while allowing
        // re-nomination after a cancellation.
        Nomination nomination = existing.orElseGet(Nomination::new);
        nomination.setTraining(training);
        nomination.setOfficer(officer);
        nomination.setNominatedByDepartment(department);
        nomination.setNominationDate(LocalDateTime.now());
        nomination.setStatus(status);

        return nominationRepository.save(nomination);
    }

    @Transactional
    public Nomination cancelNomination(Long nominationId) {

        Nomination nomination = nominationRepository
                .findById(nominationId)
                .orElseThrow(() ->
                        new RuntimeException("Nomination not found")
                );

        if (nomination.getStatus() == NominationStatus.CANCELLED) {
            throw new RuntimeException("Nomination is already cancelled");
        }

        // Lock the training row before freeing/promoting seats so this can't
        // race a concurrent nomination for the same training.
        Training training = trainingRepository
                .findByIdForUpdate(nomination.getTraining().getId())
                .orElseThrow(() ->
                        new RuntimeException("Training not found")
                );

        nomination.setStatus(NominationStatus.CANCELLED);
        nominationRepository.save(nomination);

        promoteWaitingList(training);

        return nomination;
    }

    // Fills every free confirmed seat on a training from the front of its
    // waiting list, in the order nominations were received. Called after a
    // cancellation (one seat freed) and after a training's max capacity is
    // increased (possibly several seats freed at once).
    @Transactional
    public void promoteWaitingList(Training training) {

        long confirmedCount =
                nominationRepository.countByTrainingIdAndStatus(
                        training.getId(),
                        NominationStatus.CONFIRMED
                );

        long freeSeats = training.getMaxParticipants() - confirmedCount;

        while (freeSeats > 0) {

            Optional<Nomination> next =
                    nominationRepository
                        .findFirstByTrainingIdAndStatusOrderByNominationDateAscIdAsc(
                                training.getId(),
                                NominationStatus.WAITING
                        );

            if (next.isEmpty()) {
                break;
            }

            Nomination promoted = next.get();
            promoted.setStatus(NominationStatus.CONFIRMED);
            nominationRepository.save(promoted);

            freeSeats--;
        }
    }

    public List<Nomination> getConfirmedList(Long trainingId) {
        return nominationRepository
                .findByTrainingIdAndStatusOrderByNominationDateAscIdAsc(
                        trainingId,
                        NominationStatus.CONFIRMED
                );
    }

    public List<Nomination> getWaitingList(Long trainingId) {
        return nominationRepository
                .findByTrainingIdAndStatusOrderByNominationDateAscIdAsc(
                        trainingId,
                        NominationStatus.WAITING
                );
    }
}
