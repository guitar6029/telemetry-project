# Context Management

## What context means

**Context is the information available to an agent for its current execution.**

It can come from several layers:

```text
GLOBAL
  ↓
PROJECT
  ↓
ROLE
  ↓
JOB
  ↓
TASK
  ↓
RUNTIME STATE
```

Typical context includes:

- persistent role instructions and project conventions
- the current story/job and acceptance criteria
- relevant architecture and contracts
- previous artifacts and verified results
- retrieved source code or documentation
- current job status, attempt, dependencies, and outputs

Context is not the same as memory. **Memory is durable information that can be retrieved later; context is what has been selected and made available now.**

```text
Memory / Knowledge Store
        ↓
    Retrieval
        ↓
 Relevant + Authorized Context
        ↓
       Agent
```

RAG is one way to implement this retrieval pattern. It is not magic memory; it is a mechanism for finding information and putting relevant results into the current context.

## Minimum sufficient context

More context is not automatically better.

Giving an agent the entire project history can increase:

- token/inference cost
- latency
- irrelevant information
- conflicting instructions
- stale information
- sensitive-data exposure
- reasoning complexity

Giving an agent too little context can also be harmful:

- more retrieval/tool calls
- more latency
- additional inference cost
- missed dependencies
- incorrect assumptions
- repeated investigation

The goal is therefore:

> **Minimum sufficient context + controlled retrieval + least privilege.**

The context builder should select information that is relevant, authoritative, current, and authorized for the job.

## Context quality

A useful context item should be evaluated by more than relevance:

- **Relevance** — does the worker need it?
- **Authority** — is this the source of truth?
- **Freshness** — is it still current?
- **Scope** — does it apply to this project/job/role?
- **Provenance** — where did it come from?
- **Version** — which version of the artifact or contract is this?

Current authoritative project artifacts should generally outrank old conversation history or stale notes.

## Context is not a security boundary

Withholding context can reduce accidental exposure, but it is not sufficient security.

```text
Instructions → what the agent should do
Permissions   → what the agent can actually do
```

An agent that cannot see production credentials should not receive them. More importantly, an agent must not have technical permission to perform destructive production operations merely because an instruction says not to.

This is the beginning of the least-privilege model:

```text
Worker
  ├── required context
  ├── required tools
  ├── required data access
  └── required permissions
```

Not:

```text
Worker
  └── entire project + entire infrastructure + hope it behaves
```

## Context and agent handoffs

Agents should preferably communicate through durable, structured artifacts and workflow state instead of dumping unrestricted conversation history into the next worker.

Example:

```text
Backend Agent
  ↓
API Contract
  ↓
Frontend Agent
  ↓
Implementation
  ↓
Test Agent
```

The frontend worker needs the API contract and relevant project conventions. It does not automatically need the backend worker's entire reasoning history.

This keeps handoffs smaller, more reproducible, and easier to verify.

## Temporal and synchronous coordination

Context can also contain **timing and dependency information**.

A lead worker may ask another worker:

> "How long until your artifact is ready?"

If Worker A reports an estimated completion time, that information becomes workflow state that other workers can use to coordinate their own work.

Kitchen analogy:

```text
Lead Chef
   │
   ├── Steak Chef → "5 minutes"
   │
   ├── Sauce Chef → prepare now, finish near plating
   │
   ├── Salad Chef → start based on desired freshness
   │
   └── Fry Chef → time cooking close to plating
```

The lead chef is not merely collecting status. The estimate creates temporal constraints for other workers.

Software equivalent:

```text
Backend Agent
  → "API contract ready in ~20 min"
             ↓
Orchestrator
  ├── Frontend waits for contract
  ├── Test worker waits for implementation
  └── Documentation worker can work independently
```

This is a transition from simple agent execution toward **workflow orchestration**: dependencies, readiness, timing, state, and handoffs become first-class concerns.

## Context builder

A future orchestration system can construct context per job:

```text
Job
 ↓
Context Builder
 ↓
Role Instructions
+ Story
+ Acceptance Criteria
+ Dependencies
+ Relevant Artifacts
+ Authoritative Source
+ Runtime State
+ Authorized Tools/Access
 ↓
Worker
```

Telemetry does not currently implement a general context-builder service. This is a conceptual target for the AI Engineering learning path.

## Core principles

1. Context is what an agent knows now.
2. Memory is durable information that can be retrieved later.
3. More context is not necessarily better.
4. Context selection has cost, security, quality, latency, and freshness tradeoffs.
5. Artifacts are usually better handoffs than unrestricted conversation history.
6. Use least privilege for both context and capabilities.
7. **Context restriction is not a security boundary; technical permissions must enforce security.**
8. Temporal information can become workflow state used by other workers.
9. The goal is minimum sufficient context, not minimum context.
