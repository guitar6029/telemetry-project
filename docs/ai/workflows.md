# Workflows and Orchestration

## Work moves through a system

```text
Project Management
  ↓
Manager / Planner
  ↓
Jobs
  ↓
Specialized Workers
  ↓
Artifacts
  ↓
Verification
  ↓
Human Approval
  ↓
Completion
```

Orchestration coordinates jobs, dependencies, state, and handoffs. Task decomposition turns a broad outcome into bounded pieces with clear ownership and acceptance criteria.

## Example: Telemetry Device Import

The Device Import work was approached incrementally rather than asking one agent to implement everything:

```text
Manager / Planner
  ↓
DI-1 planning
  ↓
DI-2 backend validation/submission
  ↓
DI-3 import processing
  ↓
DI-4 results
  ↓
DI-5 frontend
  ↓
DI-6 testing
```

The sequence makes dependencies and review points visible. Each job can use a suitable specialist and receive the relevant requirements, contracts, and prior artifacts.

## Engineering principles

- Keep jobs bounded and acceptance criteria explicit.
- Prefer small, reviewable changes.
- Inspect the repository before coding.
- Produce durable artifacts and record handoffs.
- Run the required tests and inspect diffs.
- Use human review where required and iterate when verification finds gaps.
- Merge only after verification and required approval.
- Give each worker relevant context, not the entire project or every prior conversation by default.

This resembles distributed workflow orchestration, with an important difference: some workers are probabilistic AI systems. Their output needs verification, explicit boundaries, and useful failure paths.
