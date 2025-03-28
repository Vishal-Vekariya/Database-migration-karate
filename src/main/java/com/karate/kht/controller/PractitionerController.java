package com.karate.kht.controller;

import com.karate.kht.entity.PractitionerEntity;
import com.karate.kht.service.PractitionerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/practitioners", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PractitionerController {
    private final PractitionerService service;

    @GetMapping
    public List<PractitionerEntity> getPractitioners() {
        return service.getPractitioners();
    }

    @GetMapping("/{id}")
    public PractitionerEntity getPractitioner(@PathVariable Long id) {
        return service.getPractitionerById(id);
    }

    @GetMapping("/search")
    public List<PractitionerEntity> searchPractitioners(@RequestParam String name) {
        return service.getPractitionersByFirstOrLastName(name);
    }

    @GetMapping("/count")
    public Long getPractitionerCount() {
        return service.getPractitionerCount();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PractitionerEntity> addPractitioner(@RequestBody PractitionerEntity practitioner) {
        PractitionerEntity created = service.createPractitioner(practitioner);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PractitionerEntity> updatePractitioner(@PathVariable Long id, @RequestBody PractitionerEntity practitioner) {
        PractitionerEntity updated = service.updatePractitioner(id, practitioner);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePractitioner(@PathVariable Long id) {
        if (service.getPractitionerById(id) != null) {
            service.deletePractitioner(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
