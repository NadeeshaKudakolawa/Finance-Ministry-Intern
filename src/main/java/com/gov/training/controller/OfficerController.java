package com.gov.training.controller;

import com.gov.training.entity.Officer;
import com.gov.training.repository.OfficerRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/officers")
@CrossOrigin(origins = "*")
public class OfficerController {

    private final OfficerRepository officerRepository;

    public OfficerController(
            OfficerRepository officerRepository
    ) {
        this.officerRepository = officerRepository;
    }

    @GetMapping
    public List<Officer> getAll() {
        return officerRepository.findAll();
    }

    @PostMapping
    public Officer create(
            @RequestBody Officer officer
    ) {
        return officerRepository.save(officer);
    }
}
