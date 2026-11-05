package com.relieflink.request;

import com.relieflink.ReliefLinkApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test skeleton for RequestController.
 *
 * <p>Uses {@code @SpringBootTest} with MockMvc for end-to-end testing.
 * To run against a real database, configure a test profile with
 * H2 or Testcontainers.</p>
 *
 * <p><b>Note:</b> These tests verify security rules — unauthenticated
 * requests should receive 401/403 responses.</p>
 */
@SpringBootTest(classes = ReliefLinkApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RequestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/requests without auth should return 401")
    void createRequest_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"resource_id\":1,\"location_id\":1,\"quantity_requested\":10}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/admin/requests without auth should return 401")
    void getAdminRequests_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/admin/requests"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/volunteers/requests without auth should return 401")
    void getVolunteerRequests_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/volunteers/requests"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login with valid endpoint should return 400 for empty body")
    void login_EmptyBody_Returns400() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register endpoint is accessible")
    void register_EmptyBody_Returns400() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
