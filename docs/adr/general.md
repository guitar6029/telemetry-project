## Authentication

### Registration

The platform supports user registration through:

`POST /api/v1/auth/register`

The registration flow:

1. Validates the registration request.
2. Normalizes the user's email address.
3. Checks for an existing user with the same email.
4. Hashes the password using BCrypt.
5. Assigns the default avatar URL.
6. Persists the user.

### Login

The platform supports login through:

`POST /api/v1/auth/login`

The login flow:

1. Normalizes the supplied email.
2. Looks up the user by email.
3. Verifies the supplied password against the BCrypt password hash.
4. Generates a signed JWT access token.
5. Stores the JWT in an `access_token` HttpOnly cookie using the
   `Set-Cookie` response header.

The JWT is not returned to or stored by the Angular application.

### Authentication Filter

Protected requests pass through `JwtAuthenticationFilter`.

The filter:

1. Reads cookies from the incoming request.
2. Locates the `access_token` cookie.
3. Extracts and validates the JWT.
4. Extracts the user UUID from the JWT `sub` claim.
5. Loads the corresponding user.
6. Creates a Spring Security `Authentication`.
7. Stores the authentication in the `SecurityContext`.

If no valid authentication cookie is present, the request continues without
an authenticated security context. Spring Security determines whether the
requested endpoint permits anonymous access.

### Security Configuration

The authentication endpoints are publicly accessible:

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

Other API endpoints require authentication unless explicitly configured
otherwise.

CORS is enabled through Spring Security to support requests from the Angular
development application.

CSRF is currently disabled during development. Cookie security settings such
as `Secure`, `SameSite`, expiration, logout behavior, and production CSRF
protection will be addressed as the authentication system evolves.
