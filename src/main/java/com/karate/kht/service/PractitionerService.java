package com.karate.kht.service;

import com.karate.kht.entity.PractitionerEntity;
import com.karate.kht.repository.PractitionerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PractitionerService {
    private final PractitionerRepository repository;

    public List<PractitionerEntity> getPractitioners() {
        return repository.findAll();
    }

    public PractitionerEntity getPractitionerById(Long id) {
        return repository
                .findById(id)
                .orElse(null);
    }

    public List<PractitionerEntity> getPractitionersByFirstOrLastName(String name) {
        return repository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    public Long getPractitionerCount() {
        return repository.count();
    }

    public PractitionerEntity createPractitioner(PractitionerEntity practitioner) {
        PractitionerEntity newPractitioner = new PractitionerEntity();
        newPractitioner.setFirstName(practitioner.getFirstName());
        newPractitioner.setLastName(practitioner.getLastName());
        newPractitioner.setStyle(practitioner.getStyle());
        newPractitioner.setImageUrl(practitioner.getImageUrl());
        newPractitioner.setRegion(practitioner.getRegion());
        newPractitioner.setBiography(practitioner.getBiography());
        newPractitioner.setYearOfBirth(practitioner.getYearOfBirth());
        newPractitioner.setYearOfDeath(practitioner.getYearOfDeath());

        return repository.save(newPractitioner);
    }

    public PractitionerEntity updatePractitioner(Long id, PractitionerEntity updatedPractitioner) {
        PractitionerEntity existingPractitioner = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Practitioner with ID " + id + " not found"));

        if (isValid(updatedPractitioner.getFirstName()))
            existingPractitioner.setFirstName(updatedPractitioner.getFirstName());

        if (isValid(updatedPractitioner.getLastName()))
            existingPractitioner.setLastName(updatedPractitioner.getLastName());

        if (isValid(updatedPractitioner.getStyle()))
            existingPractitioner.setStyle(updatedPractitioner.getStyle());

        if (updatedPractitioner.getYearOfBirth() != null)
            existingPractitioner.setYearOfBirth(updatedPractitioner.getYearOfBirth());

        if (updatedPractitioner.getYearOfDeath() != null)
            existingPractitioner.setYearOfDeath(updatedPractitioner.getYearOfDeath());

        if (isValid(updatedPractitioner.getBiography()))
            existingPractitioner.setBiography(updatedPractitioner.getBiography());

        if (isValid(updatedPractitioner.getRegion()))
            existingPractitioner.setRegion(updatedPractitioner.getRegion());

        if (isValid(updatedPractitioner.getImageUrl()))
            existingPractitioner.setImageUrl(updatedPractitioner.getImageUrl());

        return repository.save(existingPractitioner);
    }

    // Utility method to check if a string is not null and not empty
    private boolean isValid(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public void deletePractitioner(Long id) {
        repository.deleteById(id);
    }
}

