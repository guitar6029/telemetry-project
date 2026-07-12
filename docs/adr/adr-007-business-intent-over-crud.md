## Business Intent Over CRUD

Context

Generic CRUD endpoints expose implementation details but not business workflows.

Enterprise platforms communicate user intent through domain-specific actions.

Decision

When appropriate, APIs should expose business operations instead of only CRUD semantics.

Examples:

Instead of only:

```
PUT /templates/{id}
```

the platform may expose:

```
POST /templates/{id}/duplicate

POST /templates/{id}/archive

POST /devices/{id}/assign-template
```

Consequences

Pros

Easier to understand
Better alignment with business workflows
Easier frontend implementation

Cons

More endpoints
Requires stronger domain modeling

```
Business Problem
        ↓
Business Rules
        ↓
Architecture
        ↓
Backend API
        ↓
Frontend UX
        ↓
User Experience

```
