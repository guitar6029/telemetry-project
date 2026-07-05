## Docker Compose Responsibilities

Docker Compose orchestrates containers.

Responsibilities include:

- Networks
- Volumes
- Environment variables
- Build configuration
- Container startup order

Docker Compose tells Docker:

- where the application lives (context)
- which Dockerfile to use

The Dockerfile should remain focused on building a single application.
