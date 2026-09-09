package com.gov.training.controller;

import com.gov.training.entity.EligibilityRuleConfig;
import com.gov.training.repository.EligibilityRuleConfigRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eligibility-rules")
@CrossOrigin(origins = "*")
public class EligibilityRuleController {

    private final EligibilityRuleConfigRepository repository;

    public EligibilityRuleController(EligibilityRuleConfigRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EligibilityRuleConfig> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public EligibilityRuleConfig create(@RequestBody EligibilityRuleConfig rule) {
        rule.setId(null);
        return repository.save(rule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
