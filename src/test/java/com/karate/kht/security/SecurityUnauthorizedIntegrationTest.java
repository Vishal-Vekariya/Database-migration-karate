package com.karate.kht.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karate.kht.entity.PractitionerEntity;
import com.karate.kht.entity.UserEntity;
import com.karate.kht.model.Role;
import com.karate.kht.repository.PractitionerRepository;
import com.karate.kht.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test") // Ensures Spring loads application-test.yml
@AutoConfigureMockMvc
public class SecurityUnauthorizedIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private PractitionerEntity testPractitioner;
    private UserEntity testUser;
    private PractitionerEntity newPractitioner;
    private UserEntity newUser;

    @BeforeEach
    void setUp() {
        // Prepare test data before each test
        testPractitioner = new PractitionerEntity();
        testPractitioner.setFirstName("Karate");
        testPractitioner.setLastName("Practitioner");
        testPractitioner.setYearOfBirth(1985);
        testPractitioner.setYearOfDeath(2070);
        testPractitioner.setRegion("Ottawa");
        testPractitioner.setBiography("Test bio");
        testPractitioner.setStyle("Karate");
        testPractitioner.setImageUrl("https://karate.io/images/karate.png");

        practitionerRepository.save(testPractitioner);

        testUser = new UserEntity();
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setUsername("janedoe");
        testUser.setRole(Role.USER);
        testUser.setEmail("janedoe@gmail.com");
        testUser.setKarateOrganization("test organization");
        testUser.setPassword("password");

        userRepository.save(testUser);

        newPractitioner = new PractitionerEntity();

        newPractitioner.setFirstName("Kungfu");
        newPractitioner.setLastName("Panda");
        newPractitioner.setYearOfBirth(1990);
        newPractitioner.setYearOfDeath(2080);
        newPractitioner.setRegion("Toronto");
        newPractitioner.setBiography("New bio");
        newPractitioner.setStyle("Kungfu");
        newPractitioner.setImageUrl("https://karate.io/images/new.png");

        newUser = new UserEntity();

        newUser.setFirstName("John");
        newUser.setLastName("Smith");
        newUser.setUsername("johnsmith");
        newUser.setRole(Role.USER);
        newUser.setEmail("johnsmith@gmail.com");
        newUser.setKarateOrganization("dojo");
        newUser.setPassword("password");
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
        userRepository.deleteAll();
        practitionerRepository.deleteAll();
    }

    @Test
    void unauthorizedCanCreateUser() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void unauthorizedCanGetPractitioners() throws Exception {
        mockMvc.perform(get("/practitioners"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Karate"));
    }

    @Test
    void unauthorizedCanGetPractitionerById() throws Exception {
        mockMvc.perform(get("/practitioners/" + testPractitioner.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Karate"));
    }

    @Test
    void unauthorizedCannotCreatePractitioner() throws Exception {
        mockMvc.perform(post("/practitioners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPractitioner)))
                .andDo(print())
                .andExpect(status().isMovedTemporarily());
    }

    @Test
    void unauthorizedCannotUpdatePractitioner() throws Exception {
        testPractitioner.setFirstName("Updated Jane");

        mockMvc.perform(put("/practitioners/{id}", testPractitioner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPractitioner)))
                .andDo(print())
                .andExpect(status().isMovedTemporarily());
    }

    @Test
    void unauthorizedCannotDeletePractitioner() throws Exception {
        mockMvc.perform(delete("/practitioners/{id}", testPractitioner.getId())
                )
                .andDo(print())
                .andExpect(status().isMovedTemporarily());
    }

    @Test
    void unauthorizedCannotGetUsers() throws Exception {
        mockMvc.perform(get("/users")
                )
                .andDo(print())
                .andExpect(status().isMovedTemporarily());
    }

    @Test
    void unauthorizedCannotGetUserById() throws Exception {
        mockMvc.perform(get("/users/{id}", testUser.getId())
                )
                .andDo(print())
                .andExpect(status().isMovedTemporarily());
    }

    @Test
    void unauthorizedCannotUpdateUser() throws Exception {
        testUser.setFirstName("Updated User");

        mockMvc.perform(put("/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andDo(print())
                .andExpect(status().isMovedTemporarily());
    }

    @Test
    void unauthorizedCannotDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", testUser.getId())
                )
                .andDo(print())
                .andExpect(status().isMovedTemporarily());
    }
}
