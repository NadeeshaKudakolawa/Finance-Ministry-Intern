package com.gov.training.controller;

import com.gov.training.dto.NominationRequest;
import com.gov.training.entity.Nomination;
import com.gov.training.repository.NominationRepository;
import com.gov.training.service.NominationService;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nominations")
@CrossOrigin(origins = "*")
public class NominationController {

    private final NominationService nominationService;
    private final NominationRepository nominationRepository;

    public NominationController(
            NominationService nominationService,
            NominationRepository nominationRepository
    ) {
        this.nominationService = nominationService;
        this.nominationRepository = nominationRepository;
    }

    @GetMapping
    public List<Nomination> getAll() {
        return nominationRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createNomination(
            @RequestBody NominationRequest request
    ) {

        try {

            Nomination nomination =
                    nominationService
                            .createNomination(request);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(nomination);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id
    ) {
        try {

            Nomination cancelled =
                    nominationService.cancelNomination(id);

            return ResponseEntity.ok(cancelled);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    @GetMapping("/training/{trainingId}/confirmed")
    public List<Nomination> getConfirmedList(
            @PathVariable Long trainingId
    ) {
        return nominationService.getConfirmedList(trainingId);
    }

    @GetMapping("/training/{trainingId}/waitlist")
    public List<Nomination> getWaitingList(
            @PathVariable Long trainingId
    ) {
        return nominationService.getWaitingList(trainingId);
    }
}
