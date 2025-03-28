package com.karate.kht.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karate.kht.entity.PractitionerEntity;
import com.karate.kht.service.PractitionerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PractitionerController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security for testing
public class PractitionerControllerTest {

    PractitionerEntity newPractitioner = new PractitionerEntity();
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PractitionerService practitionerService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        newPractitioner.setId(1L);
        newPractitioner.setFirstName("Jane");
        newPractitioner.setLastName("Doe");
        newPractitioner.setYearOfBirth(2000);
        newPractitioner.setYearOfDeath(3000);
        newPractitioner.setRegion("Ottawa");
        newPractitioner.setBiography("nihao");
        newPractitioner.setStyle("Kungfu");
        newPractitioner.setImageUrl("https://karate.io/images/karate.png");
    }

    @Test
    void testGetPractitioners() throws Exception {
        List<PractitionerEntity> practitioners = List.of(newPractitioner);

        when(practitionerService.getPractitioners()).thenReturn(practitioners);

        mockMvc.perform(get("/practitioners"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value(newPractitioner.getFirstName()));
    }

    @Test
    void testGetPractitionerCount() throws Exception {
        when(practitionerService.getPractitionerCount()).thenReturn(10L);

        mockMvc.perform(get("/practitioners/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    void testGetPractitionerById() throws Exception {
        when(practitionerService.getPractitionerById(1L)).thenReturn(newPractitioner);

        mockMvc.perform(get("/practitioners/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(newPractitioner.getFirstName()));
    }

    @Test
    void testGetPractitionersByFirstOrLastName() throws Exception {
        when(practitionerService.getPractitionersByFirstOrLastName("jane")).thenReturn(List.of(newPractitioner));

        mockMvc.perform(get("/practitioners/search?name=jane"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value(newPractitioner.getFirstName()));
    }

    @Test
    void testCreatePractitioner() throws Exception {
        // given - precondition or setup
        given(practitionerService.createPractitioner(any(PractitionerEntity.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/practitioners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPractitioner)));

        // then - verify the result or output using assert statements
        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(newPractitioner.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(newPractitioner.getLastName()));
    }

    @Test
    void testUpdatePractitioner() throws Exception {
        PractitionerEntity updatedPractitioner = new PractitionerEntity();

        updatedPractitioner.setId(1L);
        updatedPractitioner.setFirstName("John");

        when(practitionerService.updatePractitioner(eq(1L), any())).thenReturn(updatedPractitioner);

        mockMvc.perform(put("/practitioners/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedPractitioner)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(updatedPractitioner.getFirstName()));
    }


    @Test
    void testDeletePractitioner() throws Exception {
        // Mock the service to return a practitioner when getPractitionerById is called
        when(practitionerService.getPractitionerById(1L)).thenReturn(new PractitionerEntity());

        // Mock delete method to do nothing
        doNothing().when(practitionerService).deletePractitioner(1L);

        mockMvc.perform(delete("/practitioners/1"))
                .andDo(print())
                .andExpect(status().isNoContent()); // Expect 204 No Content
    }
}
