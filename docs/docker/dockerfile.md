## Dockerfile

The Dockerfile contains the instructions for building an image.

Example:

```dockerfile
COPY pom.xml .
COPY src src
```

The Dockerfile assumes these files exist inside the build context.

The Dockerfile does **not** determine where Docker looks for files.

The build context does.
