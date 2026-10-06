# Agents

## Conceptual model

```text
Agent =
    Model
    + Instructions
    + Context
    + Tools
    + State
    + Permissions
    + Goal
    + Execution Loop
```

An agent is an execution system around a model, not simply an LLM with a large prompt. The surrounding system supplies a goal, selects context, provides tools and permissions, tracks state, and decides when to stop or ask for help.

## Agent loop

```text
Goal
  ↓
Think
  ↓
Act
  ↓
Observe
  ↓
Done?
  ├── No → continue
  └── Yes → finish
```

The loop is conceptual. A production implementation needs bounded execution, observable actions, and clear stop and escalation conditions.

## Specialized roles

Potential roles include backend-engineer, frontend-engineer, UI-engineer, UX-engineer, QA-engineer, DevOps-engineer, security-reviewer, database-engineer, architect, and researcher. Specialization can make responsibilities and expected artifacts clearer; it does not guarantee quality.

More agents do not automatically make a better system. Additional agents introduce coordination overhead, duplicated context, communication complexity, conflicts, increased cost, and debugging complexity. Use the smallest set of roles that helps the work.

Use AI for work that needs interpretation, synthesis, or judgment. Use deterministic software for deterministic work. For example, running Maven tests is a deterministic job for a tool or workflow; an agent should not decide whether Maven tests should exist.

Agents should exchange structured artifacts and durable state where possible, rather than relying only on unrestricted conversation history. Examples include requirements, architecture notes, API contracts, database schemas, UI specifications, implementations, tests, and review reports. Select context intentionally: relevant context is better than maximum context.
