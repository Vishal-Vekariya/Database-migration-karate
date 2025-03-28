package com.karate.kht.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karate.kht.entity.UserEntity;
import com.karate.kht.model.Role;
import com.karate.kht.repository.UserRepository;
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
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // Prepare test data before each test
        testUser = new UserEntity();
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setUsername("janedoe");
        testUser.setRole(Role.USER);
        testUser.setEmail("janedoe@gmail.com");
        testUser.setKarateOrganization("test organization");
        testUser.setPassword("password");

        userRepository.save(testUser);
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
        userRepository.deleteAll();
    }

    @WithMockUser(username = "testAdmin", roles = {"ADMIN"}) // mock authorization
    @Test
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value("Jane"));
    }

    @WithMockUser(username = "testAdmin", roles = {"ADMIN"}) // mock authorization
    @Test
    void testGetUsersById() throws Exception {
        mockMvc.perform(get("/users/{id}", testUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    void testCreateUser() throws Exception {
        UserEntity newUser = new UserEntity();
        newUser.setFirstName("John");
        newUser.setLastName("Smith");
        newUser.setUsername("johnsmith");
        newUser.setRole(Role.USER);
        newUser.setEmail("johnsmith@gmail.com");
        newUser.setKarateOrganization("dojo");
        newUser.setPassword("password");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @WithMockUser(username = "testAdmin", roles = {"ADMIN"}) // mock authorization
    @Test
    void testUpdateUser() throws Exception {
        testUser.setFirstName("Updated Jane");

        mockMvc.perform(put("/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated Jane"));
    }

    @WithMockUser(username = "testAdmin", roles = {"ADMIN"}) // mock authorization
    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", testUser.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @WithMockUser(username = "testAdmin", roles = {"ADMIN"}) // mock authorization
    @Test
    @DisplayName("GET /users/{id} - Non-existent ID")
    public void getNonExistentUser() throws Exception {
        long nonExistentId = 9999L; // non-existent ID

        mockMvc.perform(get("/users/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()) // Expect HTTP 200
                .andExpect(content().string("")); // Expect empty response body (null)
    }

    @WithMockUser(username = "testAdmin", roles = {"ADMIN"}) // mock authorization
    @Test
    @DisplayName("DELETE /users/{id} - Non-existent ID should return 404")
    public void deleteNonExistentUser_ShouldReturn404() throws Exception {
        long nonExistentId = 9999L; // non-existent ID

        mockMvc.perform(delete("/users/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound()); // Expect HTTP 404
    }
}
