package com.joshsoll.telemetry.platform.organization.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.joshsoll.telemetry.platform.organizationmembership.repository.OrganizationMembershipRepository;
import com.joshsoll.telemetry.platform.auth.entity.User;
import com.joshsoll.telemetry.platform.auth.enums.PlatformRole;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.joshsoll.telemetry.platform.common.response.PagedApiResponse;
import com.joshsoll.telemetry.platform.organization.dto.CreateOrganizationRequest;
import com.joshsoll.telemetry.platform.organization.dto.OrganizationResponse;
import com.joshsoll.telemetry.platform.organization.entity.Organization;
import com.joshsoll.telemetry.platform.organization.exception.OrganizationNotFoundException;
import com.joshsoll.telemetry.platform.organization.repository.OrganizationRepository;

@ExtendWith(MockitoExtension.class)
public class OrganizationServiceTest {
        @Mock
        private OrganizationMembershipRepository organizationMembershipRepository;

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

                OrganizationResponse response = organizationService.getOrganizationById(organization.getId());

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
                assertThrows(OrganizationNotFoundException.class, () -> {
                        organizationService.getOrganizationById(id);
                });
        }

        @Test
        void shouldCreateANewOrganization() {

                // create dummy organization
                Organization organization = new Organization("MyOrganization", "my-organization", Instant.now(),
                                Instant.now());

                // call org repo to save this organization
                when(organizationRepository.save(any(Organization.class)))
                                .thenReturn(organization);

                CreateOrganizationRequest request = new CreateOrganizationRequest(
                                "MyOrganization",
                                "my-organization");

                OrganizationResponse response = organizationService.createOrganization(request);

                assertEquals(request.getName(), response.name());
                assertEquals(request.getSlug(), response.slug());
                assertNotNull(response.createdAt());
                assertNotNull(response.updatedAt());

                verify(organizationRepository)
                                .save(any(Organization.class));

        }

        @Test
        void shouldReturnAllOrganizationsForSuperAdmin() {

                User user = mock(User.class);

                when(user.getPlatformRole())
                                .thenReturn(PlatformRole.SUPER_ADMIN);

                Organization org1 = new Organization(
                                "OpenAI",
                                "openai",
                                Instant.now(),
                                Instant.now());

                Organization org2 = new Organization(
                                "Google",
                                "google",
                                Instant.now(),
                                Instant.now());

                List<Organization> orgs = List.of(org1, org2);

                Page<Organization> page = new PageImpl<>(orgs);

                when(organizationRepository.findAll(any(Pageable.class)))
                                .thenReturn(page);

                PagedApiResponse<OrganizationResponse> response = organizationService.getOrganizations(user, 0, 10);

                assertEquals(2, response.getData().size());
                assertEquals("OpenAI", response.getData().get(0).name());
                assertEquals("Google", response.getData().get(1).name());
                assertEquals(0, response.getPage());
                assertEquals(10, response.getSize());

                verify(organizationRepository)
                                .findAll(any(Pageable.class));
        }

        @Test
        void shouldReturnOnlyAccessibleOrganizationsForUser() {

                User user = mock(User.class);
                UUID userId = UUID.randomUUID();

                when(user.getId()).thenReturn(userId);
                when(user.getPlatformRole()).thenReturn(PlatformRole.USER);

                Organization org1 = new Organization(
                                "Organization One",
                                "organization-one",
                                Instant.now(),
                                Instant.now());

                Organization org2 = new Organization(
                                "Organization Two",
                                "organization-two",
                                Instant.now(),
                                Instant.now());

                Page<Organization> page = new PageImpl<>(List.of(org1, org2));

                when(organizationMembershipRepository.findOrganizationsByUserId(
                                org.mockito.ArgumentMatchers.eq(userId),
                                any(Pageable.class)))
                                .thenReturn(page);

                PagedApiResponse<OrganizationResponse> response = organizationService.getOrganizations(user, 0, 10);

                assertEquals(2, response.getData().size());
                assertEquals("Organization One", response.getData().get(0).name());
                assertEquals("Organization Two", response.getData().get(1).name());

                verify(organizationMembershipRepository)
                                .findOrganizationsByUserId(
                                                org.mockito.ArgumentMatchers.eq(userId),
                                                any(Pageable.class));
        }

}
