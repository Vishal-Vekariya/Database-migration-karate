package com.karate.kht.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karate.kht.entity.PractitionerEntity;
import com.karate.kht.repository.PractitionerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test") // Ensures Spring loads application-test.yml
@AutoConfigureMockMvc
public class PractitionerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PractitionerRepository practitionerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private PractitionerEntity testPractitioner;

    @BeforeEach
    void setUp() {
        // Prepare test data before each test
        testPractitioner = new PractitionerEntity();
        testPractitioner.setFirstName("Jane");
        testPractitioner.setLastName("Doe");
        testPractitioner.setYearOfBirth(1985);
        testPractitioner.setYearOfDeath(2070);
        testPractitioner.setRegion("Ottawa");
        testPractitioner.setBiography("Test bio");
        testPractitioner.setStyle("Karate");
        testPractitioner.setImageUrl("https://karate.io/images/karate.png");

        practitionerRepository.save(testPractitioner);
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
        practitionerRepository.deleteAll();
    }

    @Test
    void testGetAllPractitioners() throws Exception {
        mockMvc.perform(get("/practitioners"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Jane"));
    }

    @Test
    void testGetPractitionerById() throws Exception {
        mockMvc.perform(get("/practitioners/{id}", testPractitioner.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @WithMockUser(username = "testAdmin", roles = {"ADMIN"}) // mock authorization
    @Test
    void testCreatePractitioner() throws Exception {
        PractitionerEntity newPractitioner = new PractitionerEntity();
        newPractitioner.setFirstName("John");
        newPractitioner.setLastName("Smith");
        newPractitioner.setYearOfBirth(1990);
        newPractitioner.setYearOfDeath(2080);
        newPractitioner.setRegion("Toronto");
        newPractitioner.setBiography("New bio");
        newPractitioner.setStyle("Kungfu");
        newPractitioner.setImageUrl("https://karate.io/images/new.png");

        mockMvc.perform(post("/practitioners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPractitioner)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @WithMockUser(username = "testAdmin", roles = {"ADMIN"}) // mock authorization
    @Test
    void testUpdatePractitioner() throws Exception {
        testPractitioner.setFirstName("Updated Jane");

        mockMvc.perform(put("/practitioners/{id}", testPractitioner.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testPractitioner)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated Jane"));
    }

    @WithMockUser(username = "testAdmin", roles = {"ADMIN"}) // mock authorization
    @Test
    void testDeletePractitioner() throws Exception {
        mockMvc.perform(delete("/practitioners/{id}", testPractitioner.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /practitioners/{id} - Non-existent ID")
    public void getNonExistentPractitioner() throws Exception {
        long nonExistentId = 9999L; // non-existent ID

        mockMvc.perform(get("/practitioners/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(content().string("")); // Expect empty response body (null)
    }

    @WithMockUser(username = "testAdmin", roles = {"ADMIN"}) // mock authorization
    @Test
    @DisplayName("DELETE /practitioners/{id} - Non-existent ID should return 404")
    public void deleteNonExistentPractitioner_ShouldReturn404() throws Exception {
        long nonExistentId = 9999L; // non-existent ID

        mockMvc.perform(delete("/practitioners/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound()); // Expect HTTP 404
    }
}
