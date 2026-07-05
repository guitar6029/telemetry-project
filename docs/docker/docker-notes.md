# Docker Notes

## Build Context

The build context defines Docker's workspace during the image build.

Everything inside the context is visible to the Dockerfile.

Everything outside the context is invisible.

Example:

```yaml
build:
  context: services/platform/api
  dockerfile: Dockerfile
```

Docker's workspace becomes:

services/platform/api

Docker can access:

- Dockerfile
- pom.xml
- src/
- .mvn/

Docker cannot access files outside this directory.

---

## Personal Takeaways

- The build context is one of the most important Docker concepts.
- Most COPY errors are actually context errors.
- Keep Dockerfiles relative to the application, not the repository.
- Let Docker Compose know where the application lives.
- Keep Dockerfiles portable by avoiding repository-specific paths whenever possible.
