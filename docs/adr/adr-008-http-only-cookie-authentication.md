# ADR-008: HttpOnly Cookie Authentication

## Status

Accepted

## Context

The platform uses JWT access tokens for user authentication.

The initial authentication design returned the JWT access token to the web
client, where the client would be responsible for storing the token and
sending it with requests using the `Authorization: Bearer <token>` header.

For the browser-based web application, this exposes the access token to
client-side JavaScript and requires the frontend to manage token storage and
transport.

## Decision

The platform will continue using JWT access tokens as the authentication
credential, but browser authentication will transport the JWT using an
HttpOnly cookie.

On successful login, the API:

1. Authenticates the user's credentials.
2. Generates the JWT access token.
3. Creates an `access_token` cookie.
4. Marks the cookie as `HttpOnly`.
5. Returns the cookie using the `Set-Cookie` response header.

The browser stores the cookie and handles its transport.

The JWT authentication filter reads the access token from the incoming
`access_token` cookie rather than the `Authorization` header.

## Consequences

### Benefits

- The JWT is not exposed to application JavaScript.
- The frontend does not need to store or manage the access token.
- Authentication credential transport is handled by the browser.
- Existing JWT validation and identity logic can remain unchanged.

### Tradeoffs

Cookie-based authentication introduces additional browser security
considerations, including:

- CSRF protection
- `SameSite` cookie policy
- `Secure` cookies in production
- CORS and credential configuration
- Cookie expiration
- Logout and cookie invalidation

These concerns will be addressed as the authentication system evolves.
