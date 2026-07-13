package com.joshsoll.telemetry.platform.organization.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.organization.dto.CreateOrganizationRequest;
import com.joshsoll.telemetry.platform.organization.dto.OrganizationResponse;
import com.joshsoll.telemetry.platform.organization.service.OrganizationService;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(OrganizationController.class)
class OrganizationControllerTest {
        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private OrganizationService organizationService;

        @Test
        void shouldReturnOrganizationById() throws Exception {
                UUID id = UUID.randomUUID();

                OrganizationResponse response = new OrganizationResponse(
                                id,
                                "TestDummyResponse",
                                "test-dummy-response",
                                Instant.now(),
                                Instant.now());

                when(organizationService.getOrganizationById(id))
                                .thenReturn(response);

                mockMvc.perform(get("/api/v1/organizations/{id}", id))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.name").value(response.name()))
                                .andExpect(jsonPath("$.data.slug").value(response.slug()));
        }

        @Test
        void shouldCreateOrganization() throws Exception {

                UUID id = UUID.randomUUID();

                CreateOrganizationRequest request = new CreateOrganizationRequest("TestDummyResponse",
                                "test-dummy-response");

                OrganizationResponse response = new OrganizationResponse(
                                id,
                                request.getName(),
                                request.getSlug(),
                                Instant.now(),
                                Instant.now());

                when(organizationService.createOrganization(any(CreateOrganizationRequest.class))).thenReturn(response);

                final ObjectMapper mapper = new ObjectMapper();

                mockMvc.perform(post("/api/v1/organizations").contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(request))).andExpect(status().isCreated())
                                .andDo(print())
                                .andExpect(jsonPath("$.data.name").value(response.name()))
                                .andExpect(jsonPath("$.data.slug").value(response.slug()));
        }

        @Test
        void shouldReturnPagedOrganizations() throws Exception {

                final int PAGE = 1;
                final int SIZE = 10;
                final int AMOUNT_OF_ORGS = 10;

                Instant now = Instant.now();

                List<OrganizationResponse> orgs = new ArrayList<>();

                for (int i = 1; i <= AMOUNT_OF_ORGS; i++) {
                        orgs.add(new OrganizationResponse(
                                        UUID.randomUUID(),
                                        "Org" + i,
                                        "org-" + i,
                                        now,
                                        now));
                }

                PagedApiResponse<OrganizationResponse> responses = new PagedApiResponse<>(orgs, "",
                                PAGE, SIZE,
                                orgs.size(), 2);

                when(organizationService.getOrganizations(PAGE, SIZE)).thenReturn(responses);

                mockMvc.perform(get("/api/v1/organizations")
                                .param("page", String.valueOf(PAGE))
                                .param("size", String.valueOf(SIZE))).andExpect(status().isOk())
                                .andExpect(jsonPath("$.data.length()").value(10))
                                .andExpect(jsonPath("$.data[0].name").value(orgs.get(0).name()))
                                .andExpect(jsonPath("$.data[9].name").value(orgs.get(9).name()))
                                .andExpect(jsonPath("$.page").value(PAGE))
                                .andExpect(jsonPath("$.size").value(SIZE));
        }
}
