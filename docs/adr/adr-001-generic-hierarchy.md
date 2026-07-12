# ADR-001

## Hierarchy Nodes Are Generic

## Context

## Different industries organize assets differently.

Examples:

Hospitals
Factories
Retail
Warehouses
Oil & Gas

## Hardcoding node types would tightly couple the platform to specific industries.

## Decision

Hierarchy nodes remain generic.

The platform models parent-child relationships only.

The business meaning of a node belongs to the customer.

## Consequences

Pros

Works across industries
Flexible
Simple domain model

Cons

UI cannot infer semantics automatically
Customers provide naming conventions
