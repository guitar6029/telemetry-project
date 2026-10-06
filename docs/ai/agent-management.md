# Agent Management

## Factory and worker model

| Concept | Meaning |
| --- | --- |
| **AI Factory** | The overall system for organizing and delivering AI-assisted work. |
| **Job** | What needs to be done. |
| **Agent** | A worker role applied to a job. |
| **Workflow** | How jobs fit together and move forward. |
| **Orchestrator** | The manager coordinating work and state. |
| **Artifact** | An output produced by a worker, such as a design, code change, test report, or PR. |
| **Verifier** | A check that determines whether an artifact meets requirements. |
| **Human** | Approval or escalation authority where required. |

```text
Job → Agent → Artifact → Verifier → PASS / FAIL
                                      ↓
                         Retry / Escalate / Complete
```

## Role definition and worker instance

An **agent definition** is persistent. For example, `backend-engineer` defines a role's instructions, tools, permissions, capabilities, and boundaries.

A **worker instance or session** can be temporary:

```text
backend-engineer worker #127
  picks up Job DI-3
  performs work
  produces artifact or PR
  terminates
```

Another worker can use the same role definition for a later job. The role is like a reusable badge and capability profile; the worker is the person temporarily wearing it.

> **The factory remembers; workers don't necessarily have to.**

Durable project knowledge belongs in source code, documentation, architecture decisions, stories, acceptance criteria, artifacts, PRs, tests, project-management state, and workflow state. A worker should not need to remember the entire project history. Give each worker only the context needed for its job; more context is not automatically better.
