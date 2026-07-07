package com.joshsoll.telemetry.platform.organization.service;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.joshsoll.telemetry.platform.organization.dto.OrganizationResponse;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {
    @Mock
    private OrganizationRepository organizationRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @Test
    void shouldReturnOrganizationWhenIdExists() {

        // Arrange
        Organization organization = new Organization("OpenAi", "openai", Instant.now(), Instant.now());

        // Act
        when(organizationRepository.findById(organization.getId()))
                .thenReturn(Optional.of(organization));

        OrganizationResponse response = organizationService.getOrganization(organization.getId());

        // Assert
        assertEquals(organization.getId(), response.id());
        assertEquals(organization.getName(), response.name());
        assertEquals(organization.getSlug(), response.slug());

        // Verify
        verify(organizationRepository).findById(organization.getId());

    }

    @Test
    void shouldThrowWhenOrganizationDoesNotExist() {

        // Arrange
        UUID id = UUID.randomUUID();

        when(organizationRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(
                NoSuchElementException.class,
                () -> organizationService.getOrganization(id));
    }
}
