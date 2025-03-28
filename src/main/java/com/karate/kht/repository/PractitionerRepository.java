package com.karate.kht.repository;

import com.karate.kht.entity.PractitionerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PractitionerRepository extends JpaRepository<PractitionerEntity, Long> {
    List<PractitionerEntity> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
}
