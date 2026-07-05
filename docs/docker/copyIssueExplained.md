## Why COPY failed

Original configuration:

```yaml
build:
  context: .
  dockerfile: services/platform/api/Dockerfile
```

Context was:

telemetry-project/

Docker looked for:

src/

inside

telemetry-project/

Actual location:

services/platform/api/src

Result:

COPY failed.

Solution:

```yaml
build:
  context: services/platform/api
  dockerfile: Dockerfile
```

Now Docker's workspace contains src/.

---

## COPY

COPY paths are always relative to the build context.

Example:

```dockerfile
COPY src src
```

This does NOT mean:

Copy from wherever the Dockerfile lives.

It means:

Copy from the build context.
