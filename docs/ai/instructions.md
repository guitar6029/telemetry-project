# Instructions and Tasks

> **Agent instructions describe how the worker operates. A job or story describes what the worker needs to accomplish.**

Instructions are persistent operating guidance. A task is a bounded request with a specific outcome. Mixing the two makes reusable roles hard to maintain and individual work hard to evaluate.

## Instruction hierarchy

```text
Model behavior
  ↓
System instructions
  ↓
Agent role
  ↓
Project instructions
  ↓
Task/story
  ↓
Context
  ↓
Tool permissions
  ↓
Runtime state
```

This is a reasoning aid, not a claim that every platform resolves instructions in exactly this order. Higher-level constraints apply across tasks; task details and context specialize the work. Tool permissions must be enforced by the runtime and platform.

Persistent agent instructions commonly define:

- role and responsibilities
- engineering expectations and architectural conventions
- coding standards
- allowed tools, permissions, and boundaries
- expected artifacts
- validation expectations

A job should contain the specific work being requested, relevant acceptance criteria, and the context needed to do it.

## Example: backend engineer profile

```text
Role: Backend engineer

Operate within the project's documented architecture and coding conventions.
Inspect relevant code and documentation before changing behavior. Keep changes
focused on the assigned job. Do not broaden scope or claim requirements are met
without evidence. Surface assumptions and blockers. Produce a concise summary
and reviewable diff. Run the validations requested by the job or project policy,
and report their results. Use only tools and permissions provided by the runtime.

Expected artifacts: implementation diff, validation results, and notes about
behavioral or API changes.
Boundaries: do not deploy or change unrelated infrastructure unless the job
explicitly authorizes it.
```

The task given to this role might be: “Add server-side validation for device import submissions, satisfying the acceptance criteria in story DI-2.” That task is not part of the persistent role definition.
