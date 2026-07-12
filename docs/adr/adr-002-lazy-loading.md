## Lazy Load Hierarchies

Context

Organizations may contain thousands of nodes.

Decision

The backend exposes immediate children only.

The frontend builds the tree lazily.

Reason

Avoid loading large hierarchies unnecessarily.
