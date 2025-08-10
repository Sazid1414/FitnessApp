package com.fitness_application.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AdminIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;

    @BeforeEach
    void setup() throws Exception {
        // register a regular user (non-admin)
        String userJson = "{\n" +
                "  \"email\": \"user@example.com\",\n" +
                "  \"password\": \"Password123!\",\n" +
                "  \"firstName\": \"Regular\",\n" +
                "  \"lastName\": \"User\"\n" +
                "}";
        MvcResult regResult = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andReturn();
        if (regResult.getResponse().getStatus() != 201) {
            System.out.println("Register response: " + regResult.getResponse().getContentAsString());
        }
        assertThat(regResult.getResponse().getStatus()).isEqualTo(201);

        String loginPayload = "{\"email\":\"user@example.com\",\"password\":\"Password123!\"}";
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
            .andExpect(status().isOk())
            .andReturn();
        JsonNode loginJson = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        userToken = loginJson.path("data").path("token").asText();
        assertThat(userToken).isNotBlank();
    }

    @Test
    void adminStatsForbiddenForNonAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/stats")
                .header("Authorization", "Bearer " + userToken))
            .andExpect(status().isForbidden());
    }
}
