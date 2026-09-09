package com.gov.training.controller;

import com.gov.training.entity.Department;
import com.gov.training.repository.DepartmentRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "*")
public class DepartmentController {

    private final DepartmentRepository repository;

    public DepartmentController(
            DepartmentRepository repository
    ) {
        this.repository = repository;
    }

    @GetMapping
    public List<Department> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public Department create(
            @RequestBody Department department
    ) {
        return repository.save(department);
    }

    @PutMapping("/{id}")
    public Department update(
            @PathVariable Long id,
            @RequestBody Department department
    ) {
        department.setId(id);
        return repository.save(department);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {
        repository.deleteById(id);
    }
}
