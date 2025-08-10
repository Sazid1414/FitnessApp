package com.fitness_application.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class FitnessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    private UserRegistrationDto baseUser;

    @BeforeEach
    void setup() throws Exception {
        baseUser = new UserRegistrationDto();
        baseUser.setEmail("fituser@example.com");
        baseUser.setPassword("Password123!");
        baseUser.setFirstName("Fit");
        baseUser.setLastName("User");

        // register
        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(baseUser)))
            .andReturn();
        if (regResult.getResponse().getStatus() != 201) {
            System.out.println("Register response: " + regResult.getResponse().getContentAsString());
        }
        org.assertj.core.api.Assertions.assertThat(regResult.getResponse().getStatus()).isEqualTo(201);

        // login
        var loginPayload = "{\"email\":\"" + baseUser.getEmail() + "\",\"password\":\"" + baseUser.getPassword() + "\"}";
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        token = loginJson.path("data").path("token").asText();
        assertThat(token).isNotBlank();

        // update profile with fitness data to unlock calculations
        String updateProfileJson = "{\n" +
                "  \"email\": \"" + baseUser.getEmail() + "\",\n" +
                "  \"password\": \"" + baseUser.getPassword() + "\",\n" +
                "  \"firstName\": \"Fit\",\n" +
                "  \"lastName\": \"User\",\n" +
                "  \"gender\": \"MALE\",\n" +
                "  \"height\": 180,\n" +
                "  \"weight\": 80,\n" +
                "  \"activityLevel\": \"MODERATELY_ACTIVE\",\n" +
                "  \"fitnessGoal\": \"MAINTAIN_WEIGHT\"\n" +
                "}";
        mockMvc.perform(put("/api/v1/auth/profile")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateProfileJson))
            .andExpect(status().isOk());
    }

    @Test
    void getFitnessMetricsAndRecommendations() throws Exception {
        // metrics
        mockMvc.perform(get("/api/v1/fitness/metrics")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());

        // recommendations
        mockMvc.perform(get("/api/v1/fitness/recommendations")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
