# Agent Instructions

Read this file before beginning any task in this repository.


## Git workflow

- Before modifying files, verify the current branch, worktree, and `git status`.
- Use dedicated branches or worktrees for isolated tasks.
- After a PR is merged, identify obsolete task branches and worktrees.
- Only remove a worktree after confirming its changes are committed, preserved, and merged or otherwise safely transferred.
- Never delete worktrees, branches, or uncommitted changes without explicit user approval.
- Prefer `git worktree remove` for cleanup; never manually delete a Git-managed worktree directory.
- Run `git worktree prune` only when stale worktree metadata needs cleanup.
- Never develop directly on `main`; use a dedicated branch or worktree.
- Keep changes scoped to the assigned task and preserve unrelated changes.
- Do not commit, push, merge, or create a pull request unless explicitly requested.

## Task scope

- Each implementation task must have one clearly defined objective.
- Prefer small, independently testable changes.
- Do not combine unrelated features, refactors, or fixes into one PR.
- Do not expand scope without explicit user approval.
- If a task becomes significantly larger than anticipated,
  stop and report the findings before continuing.
- Keep refactoring separate from feature development unless
  strictly necessary for the assigned task.
- Prefer incremental delivery over large implementations.

## Architecture

- Respect the existing Angular frontend and Spring Boot modular-monolith conventions.
- Keep controllers and RabbitMQ consumers thin; put business logic in services.
- PostgreSQL is the source of truth for persisted application state. Use versioned Flyway migrations for schema changes.
- RabbitMQ handles asynchronous message delivery; do not use it as the source of truth.
- Avoid unnecessary abstractions and dependencies.

## Security and infrastructure

- Never inspect or expose secrets, credentials, or key material.
- Never modify `.env` files without explicit authorization, and never commit credentials.
- Avoid destructive database or Docker operations without approval.

## Quality

- Add meaningful tests for behavior changes.
- Run appropriate focused tests and builds for the affected component; the API uses `services/platform/api/./mvnw`, and the web app uses the npm scripts in `services/platform/web/package.json`.
- Report tests or verification that were skipped or could not run.
- Do not claim successful verification without running it.

## Communication

- Keep responses concise and explain important architectural decisions.
- Report modified files, verification results, and remaining risks.
- Ask before expanding the task scope.
