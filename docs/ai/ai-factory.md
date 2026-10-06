# AI Factory

> **Status: Conceptual / Future.** Telemetry does not currently have a fully autonomous AI factory. This document records a possible architecture to guide learning and future design.

## Conceptual architecture

```text
Project Management System
        ↓
Root Manager
        ↓
Planner / Scheduler / Supervisor
        ↓
Work Queue
        ↓
Specialized Workers
        ↓
Artifacts / Code / Tests / PRs
        ↓
Verification
        ↓
Human Approval / Automated Policy
        ↓
Completion
```

The current project is an AI Engineering sandbox. We are learning through human-orchestrated agent work, with the longer-term possibility of software-assisted orchestration and, later, an AI Factory. The factory itself is not being implemented as part of this documentation.

## A possible job flow

A future root manager could inspect the current sprint, priorities, stories, bugs, spikes, dependencies, and acceptance criteria. It could decompose a story, select roles, and create bounded jobs.

For example, for **“Add recent device import results page”**, it might create:

| Job | Assigned role | Example output |
| --- | --- | --- |
| Backend | `backend-engineer` | API and implementation artifact |
| Frontend | `frontend-engineer` | Page implementation |
| QA | `QA-engineer` | Test plan and verification results |

Each worker receives only the context needed for its job, such as requirements, an API contract, relevant architecture, and dependencies. Workers produce artifacts and/or code. The system validates outputs. Failed work can be retried, reassigned, sent to another specialist, or escalated to a human.

Agents should communicate through structured artifacts and durable state rather than relying exclusively on conversational history. Examples include `Requirements.md`, `Architecture.md`, API contracts, database schemas, UI specifications, implementations, tests, and review reports. Context should be intentionally selected: relevant context is better than maximum context.

## Authority and security

Instructions are **not** a security boundary. Technical permissions must enforce what an agent can actually access or modify.

Keep these authorities distinct:

- **Orchestration authority:** deciding how jobs are created, assigned, and tracked.
- **Technical permissions:** what files, tools, services, and data a worker can access or change.
- **Production and deployment authority:** what can affect live systems.

A root manager's orchestration authority does not imply unlimited production or technical access. Deployment should generally be protected by policy and/or human approval. A future system would also need auditable actions, bounded retries, verification gates, and a safe way to stop or escalate work.

## Current, conceptual, future

- **Current:** People use AI agents for bounded engineering work and review their artifacts.
- **Conceptual:** A manager decomposes work and delegates it to role-based workers through explicit workflows.
- **Future:** Software may assist with planning, scheduling, verification, and queue management. Any implementation would need to preserve permission boundaries and human approval where required.
