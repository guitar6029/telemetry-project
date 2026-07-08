package com.joshsoll.telemetry.platform.organization.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.joshsoll.telemetry.platform.organization.dto.OrganizationResponse;
import com.joshsoll.telemetry.platform.organization.service.OrganizationService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.UUID;

@WebMvcTest(OrganizationController.class)
class OrganizationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrganizationService organizationService;

    @Test
    void shouldReturnOrganizationById() throws Exception {
        UUID id = UUID.randomUUID();
        OrganizationResponse response = new OrganizationResponse(id, "TestDummyResponse", "test-dummy-response",
                Instant.now(), Instant.now());

        when(organizationService.getOrganization(id)).thenReturn(response);

        mockMvc.perform(get("/api/v1/organizations/{id}", id)).andExpect(status().isOk())
                // .andDo(print())
                .andExpect(jsonPath("$.data.name").value(response.name()))
                .andExpect(jsonPath("$.data.slug").value(response.slug()));
    }
}
