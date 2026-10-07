package com.joshsoll.telemetry.platform.device.importer.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.repository.UserRepository;
import com.joshsoll.telemetry.platform.auth.service.JwtService;
import com.joshsoll.telemetry.platform.auth.service.TokenRevocationService;
import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.device.importer.dto.DeviceImportHistoryResponse;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportMode;
import com.joshsoll.telemetry.platform.device.importer.enums.DeviceImportStatus;
import com.joshsoll.telemetry.platform.device.importer.exception.DeviceImportNotFoundException;
import com.joshsoll.telemetry.platform.device.importer.service.DeviceImportHistoryService;

@WebMvcTest(DeviceImportHistoryController.class)
class DeviceImportHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceImportHistoryService deviceImportHistoryService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TokenRevocationService tokenRevocationService;

    @Test
    void returnsPagedHistoryUsingDefaultPageAndSize() throws Exception {
        UUID organizationId = UUID.randomUUID();
        DeviceImportHistoryResponse importResponse = response(organizationId);
        User user = org.mockito.Mockito.mock(User.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, Collections.emptyList());
        when(deviceImportHistoryService.getImports(any(User.class), eq(organizationId), eq(0), eq(20)))
                .thenReturn(new PagedApiResponse<>(List.of(importResponse), "", 0, 20, 1, 1));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/imports", organizationId)
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.data[0].importId").value(importResponse.importId().toString()))
                .andExpect(jsonPath("$.data[0].status").value("QUEUED"));

        verify(deviceImportHistoryService).getImports(any(User.class), eq(organizationId), eq(0), eq(20));
    }

    @Test
    void returnsNotFoundForMissingOrOtherOrganizationsImport() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID importId = UUID.randomUUID();
        User user = org.mockito.Mockito.mock(User.class);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, Collections.emptyList());
        when(deviceImportHistoryService.getImport(any(User.class), eq(organizationId), eq(importId)))
                .thenThrow(new DeviceImportNotFoundException(importId));

        mockMvc.perform(get("/api/v1/organizations/{organizationId}/imports/{importId}",
                        organizationId, importId)
                        .with(authentication(authentication)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    private DeviceImportHistoryResponse response(UUID organizationId) {
        return new DeviceImportHistoryResponse(
                UUID.randomUUID(), organizationId, UUID.randomUUID(), UUID.randomUUID(), "devices.csv",
                DeviceImportMode.SKIP_EXISTING, DeviceImportStatus.QUEUED,
                Instant.parse("2026-01-02T00:00:00Z"), null, null);
    }
}
