package com.karate.kht.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.karate.kht.entity.UserEntity;
import com.karate.kht.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.karate.kht.model.Role.USER;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security for testing
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetUsers() throws Exception {
        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("janedoe@gmail.com");
        user.setKarateOrganization("Karate Organization");

        List<UserEntity> users = List.of(user);

        when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$[0].lastName").value(user.getLastName()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()))
                .andExpect(jsonPath("$[0].karateOrganization").value(user.getKarateOrganization()));
    }

    @Test
    void testGetUserCount() throws Exception {
        when(userService.getUserCount()).thenReturn(10L);

        mockMvc.perform(get("/users/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    void testGetUserById() throws Exception {
        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("janedoe@gmail.com");
        user.setKarateOrganization("Karate Organization");

        when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.karateOrganization").value(user.getKarateOrganization()));
    }

    @Test
    void testCreateUser() throws Exception {
        UserEntity user = new UserEntity();

        user.setId(1L);
        user.setFirstName("Jane");
        user.setLastName("Doe");
        user.setEmail("janedoe@gmail.com");
        user.setKarateOrganization("Karate Organization");
        user.setRole(USER);
        user.setUsername("janedoe");
        user.setPassword("password");

        // given - precondition or setup
        given(userService.createUser(any(UserEntity.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        // when - action or behaviour that we are going test
        ResultActions response = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));

        // then - verify the result or output using assert statements
        response.andDo(print()).
                andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(user.getLastName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.karateOrganization").value(user.getKarateOrganization()))
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserEntity updatedUser = new UserEntity();

        updatedUser.setId(1L);
        updatedUser.setFirstName("John");

        when(userService.updateUser(eq(1L), any())).thenReturn(updatedUser);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(updatedUser.getFirstName()));
    }


    @Test
    void testDeleteUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(new UserEntity());

        // Mock delete method to do nothing
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andDo(print())
                .andExpect(status().isNoContent()); // Expect 204 No Content
    }
}
