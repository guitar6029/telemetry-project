# Telemetry Platform

The Telemetry Platform is an early-stage, multi-tenant platform for managing organizations, devices, device templates, and the telemetry those devices produce. It is intended to support many industries without baking concepts such as buildings, rooms, factories, or regions into the core data model.

> **Project status: active development / not production-ready**
>
> This is a large project under construction. Several foundational workflows are implemented, but the end-to-end telemetry product is not finished. APIs, UI flows, domain boundaries, and infrastructure are expected to evolve.

## What the project is trying to become

The platform is being built around a few durable ideas:

- Organizations define their own hierarchy for grouping assets.
- Devices hold relatively stable metadata; telemetry is append-only operational data.
- Device behavior is described through reusable device templates and metric definitions.
- Business concepts are kept separate from transport and persistence concerns.
- The first deployment shape is a modular monolith, leaving room to extract high-volume or asynchronous domains into services later.

The long-term goal is a platform that can register and organize connected devices, ingest and process telemetry, expose useful operational views, and provide commands, alerts, reporting, and automation without being tied to one industry.

## Current capabilities

The following capabilities are represented in the current codebase. “Implemented” means there is corresponding API and/or UI/domain code; it does not imply that every workflow is complete, hardened, or production-ready.

### Identity and organization management

- JWT-based authentication with login and logout flows.
- HTTP-only cookie-based session handling in the web application.
- User profile retrieval and last-used organization tracking.
- Organization creation, listing, editing, and deletion.
- Organization memberships with roles and status management.
- Organization invitations.
- Access-control checks around authenticated platform resources.

### Hierarchy and device management

- Generic, customer-defined hierarchy nodes with parent/child relationships.
- Device registration, lookup, listing, and deletion.
- Device metadata such as name, serial number, status, and organization association.
- Device assignment to a reusable device template and hierarchy node.
- Device overview and summary endpoints used by the dashboard.

### Device templates and metrics

- Device template creation, search, update, listing, and deletion.
- Metric definitions associated with device templates.
- Metric data types and validation-oriented domain objects.
- Database migrations for device templates, metric definitions, metric values, and telemetry events.

### Telemetry ingestion

- An API endpoint for accepting telemetry payloads from devices.
- Persistence of the original/raw telemetry event before processing.
- Initial telemetry processing services for deriving metric values from events.
- A normalized telemetry model designed to preserve the original transmission for later auditing and reprocessing.

The telemetry domain is still at an early stage: historical query APIs, dashboards, retention strategy, aggregation, and operational analytics are not yet complete.

### Bulk device import

- CSV-based device import from the Angular application.
- Template and hierarchy selection as part of the import flow.
- Validation and review steps before submission.
- Asynchronous import processing through RabbitMQ.
- Import status tracking, persisted errors, import history, pagination, and detail views.
- Transactional outbox components for publishing import work reliably.

### Platform foundations

- Spring Boot API with Spring MVC, validation, Spring Security, Spring Data JPA, Flyway, and AMQP.
- PostgreSQL persistence with versioned migrations.
- Angular 22 web application with route guards, lazy-loaded feature areas, reusable UI components, and responsive loading/error states.
- Docker Compose development environment for the API, web application, PostgreSQL, and RabbitMQ.
- API scripts for exercising selected organization, hierarchy, and device-template flows.
- Unit tests across selected API domains and a small initial web test suite.

## Planned and upcoming work

The roadmap below reflects the architecture and domain direction documented in the repository. Items are intentionally presented as plans, not promises or completed features.

### Telemetry product completion

- Complete metric extraction and validation for supported payloads.
- Add APIs for querying telemetry by device, metric, hierarchy, organization, and time range.
- Add historical charts, dashboards, filtering, aggregation, and export/reporting workflows.
- Define retention, indexing, partitioning, and performance strategies for growing telemetry volumes.
- Add replay and reprocessing workflows for previously received events.

### Commands, alerts, and automation

- Device command delivery and command status tracking.
- Scheduled operations such as health checks, restarts, configuration updates, and firmware windows.
- Alert rules driven by telemetry or device state.
- Notification delivery, acknowledgement, escalation, and audit history.

### Integrations and device operations

- A dedicated integration/mapping layer for external systems with inconsistent payload formats.
- Device connectivity and online/offline state tracking.
- Protocol adapters and ingestion paths beyond the initial normalized API contract.
- Firmware, configuration, and device lifecycle workflows from registration through retirement.

### Security, reliability, and scale

- More complete tenant isolation and fine-grained permissions across organizations and hierarchy scopes.
- Stronger audit logging and operational observability.
- Resilience, retry, idempotency, dead-letter, and failure-recovery strategies for asynchronous processing.
- Production deployment and infrastructure automation through Terraform.
- Performance testing and capacity planning for large device fleets.

### Product and engineering maturity

- Expand end-to-end and integration test coverage.
- Improve API documentation and local developer onboarding.
- Establish stable public contracts and versioning policy.
- Continue evaluating which domains should remain in the modular monolith and which may eventually be extracted.

The repository also contains an AI engineering knowledge base. It documents current learning and future concepts around prompts, agents, workflows, orchestration, and AI factories; those concepts are not currently implemented as Telemetry product capabilities.

## Architecture at a glance

```text
Angular web application
        │
        ▼
Spring Boot modular monolith API
  ├── authentication and authorization
  ├── organizations and memberships
  ├── generic hierarchy
  ├── devices and device templates
  ├── metric definitions and telemetry
  ├── asynchronous device imports
  └── health and profile endpoints
        │                 │
        ▼                 ▼
   PostgreSQL          RabbitMQ
```

The API is organized by domain packages rather than by one global controller/service layer. PostgreSQL stores the transactional model and Flyway manages schema evolution. RabbitMQ is used for asynchronous device-import processing; additional event-driven workloads are planned.

## Repository layout

```text
services/platform/api/   Spring Boot API and database migrations
services/platform/web/   Angular web application
docker/                  Docker Compose development environment
scripts/                 Helper scripts and API smoke-test scripts
simulator/               Planned/early device simulation area
terraform/               Planned infrastructure-as-code area
docs/                    Architecture, ADRs, testing, roadmap, and AI notes
```

Useful design references:

- [Architecture](docs/architecture.md)
- [Architecture decision records](docs/adr/)
- [Telemetry model](docs/adr/telemetry/telemetry-model.md)
- [Telemetry processing](docs/adr/telemetry/telemetry-processing.md)
- [Telemetry testing](docs/adr/telemetry/telemetry-testing.md)
- [AI engineering knowledge base](docs/ai/README.md)

## Local development

### Prerequisites

- Java 21
- Maven (or the included Maven wrapper)
- Node.js and npm compatible with the versions in `services/platform/web/package.json`
- Docker and Docker Compose

### Run the development stack

The Compose file starts PostgreSQL, RabbitMQ, the Spring Boot API, and the Angular web application. The repository intentionally does not commit environment files or JWT keys.

1. Create a root-level `.env` with the database variables referenced by `docker/docker-compose.yml`, including `DB_USER`, `DB_PASSWORD`, and `DB_NAME`.
2. Provide development JWT key files at `keys/private.pem` and `keys/public.pem`.
3. Start the stack from the repository root:

   ```bash
   ./scripts/docker-helper.sh up --build
   ```

4. Open the web application at `http://localhost:4200/`.

The API is exposed at `http://localhost:8080/`, PostgreSQL at `localhost:5432`, and RabbitMQ management at `http://localhost:15672/` when the services are running.

### Run components directly

API:

```bash
cd services/platform/api
./mvnw spring-boot:run
```

Web application:

```bash
cd services/platform/web
npm install
npm start
```

The API expects PostgreSQL, its environment variables, and JWT key paths to be available when run outside Docker.

## Verification commands

API tests:

```bash
cd services/platform/api
./mvnw test
```

Web build and tests:

```bash
cd services/platform/web
npm run build
npm test
```

## Contribution expectations

Because the platform is still taking shape, changes should include the relevant domain, API, migration, UI, and test updates together where applicable. Keep product behavior and design decisions documented in the appropriate ADR or architecture note, and clearly label conceptual or future work as such.
