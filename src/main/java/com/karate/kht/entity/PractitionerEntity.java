package com.karate.kht.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "Practitioner")
@Getter
@Setter
@ToString
public class PractitionerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    // consider using enums for this
    private String style;

    private Integer yearOfBirth;

    private Integer yearOfDeath;

    private String biography;

    private String imageUrl;

    private String region;
}
