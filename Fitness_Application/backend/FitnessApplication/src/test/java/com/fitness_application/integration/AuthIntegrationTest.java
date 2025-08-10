package com.fitness_application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness_application.dto.LoginDto;
import com.fitness_application.dto.UserRegistrationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setup() {
        registrationDto = new UserRegistrationDto();
        registrationDto.setEmail("testuser@example.com");
        registrationDto.setPassword("Password123!");
        registrationDto.setFirstName("Test");
        registrationDto.setLastName("User");
    }

    @Test
    void registerAndLoginFlow() throws Exception {
        // Register
        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDto)))
            .andReturn();
        if (regResult.getResponse().getStatus() != 201) {
            System.out.println("Register response: " + regResult.getResponse().getContentAsString());
        }
        org.assertj.core.api.Assertions.assertThat(regResult.getResponse().getStatus()).isEqualTo(201);

        // Login
        LoginDto loginDto = new LoginDto();
        loginDto.setEmail(registrationDto.getEmail());
        loginDto.setPassword(registrationDto.getPassword());

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
            .andExpect(status().isOk())
            .andReturn();

        String json = loginResult.getResponse().getContentAsString();
        assertThat(json).contains("token");
    }
}
