# Telemetry Platform Architecture

## Vision

The Telemetry Platform is designed to manage organizations, their devices, and the telemetry those devices generate.

The platform provides a flexible hierarchy that allows each organization to organize devices according to its own business structure, while collecting, storing, and visualizing telemetry data from thousands of devices.

The platform is being developed as a modular monolith with clear domain boundaries, allowing future extraction into distributed services if required.

## Core Principles

### Customer-defined Hierarchy

The platform does not model concepts such as buildings, regions, rooms, factories, or campuses.

Instead, organizations define their own hierarchy using generic hierarchy nodes.

This allows the same platform to support hospitals, construction companies, manufacturing plants, universities, retail stores, and future industries without requiring schema changes.

### Separation of Metadata and Telemetry

Devices contain metadata.

Telemetry contains measurements.

Device metadata changes infrequently.

Telemetry is append-only and continuously grows over time.

Examples of device metadata include:

- Name
- Model
- Firmware Version
- Serial Number

Examples of telemetry include:

- Temperature
- Battery
- Humidity
- Signal Strength

## Product Philosophy

The platform manages connected devices, not industries.

Organizations define their own hierarchy, terminology, and operational structure.

The platform provides generic building blocks that can support manufacturing, healthcare, construction, education, retail, smart buildings, agriculture, and future industries without requiring changes to the underlying domain model.

## Core Domains

The platform is composed of several independent domains, each responsible for a specific aspect of the system.

### Organization

Represents a customer using the platform.

Organizations own hierarchy nodes, devices, users, permissions, and operational data.

---

### Hierarchy

Provides a generic tree structure that organizations use to organize their assets.

Hierarchy nodes do not represent specific business concepts such as buildings, factories, rooms, or regions.

Instead, organizations define their own hierarchy according to their operational needs.

---

### Device

Represents a managed physical or virtual device.

Devices contain metadata describing the device but do not contain operational measurements.

Devices are assigned to hierarchy nodes and generate telemetry over time.

---

### Telemetry

Represents historical measurements produced by devices.

Telemetry is append-only and is optimized for historical analysis and monitoring.

Telemetry is considered operational data rather than configuration.

---

### Commands

Represents actions initiated by the platform and delivered to devices.

Examples include:

- Restart Device
- Update Configuration
- Request Status
- Synchronize Time

---

### Scheduling

Allows organizations to automate recurring operations.

Examples include:

- Daily Device Restart
- Weekly Health Check
- Firmware Deployment Window

---

### Alerts

Represents rules and notifications generated from telemetry or device state.

Examples include:

- Temperature exceeds threshold
- Device offline
- Battery below 20%

---

### Users

Represents authenticated users of an organization.

Users are assigned permissions that determine which hierarchy nodes and devices they may access.

## Device Lifecycle

Manufactured

↓

Registered

↓

Assigned to Organization

↓

Assigned to Hierarchy

↓

Online

↓

Receiving Commands

↓

Sending Telemetry

↓

Offline

↓

Maintenance

↓

Retired

## Domain Relationships

Organization
│
├── Users
│
├── Hierarchy
│ │
│ ├── Hierarchy
│ │ │
│ │ ├── Device
│ │ │ │
│ │ │ ├── Telemetry
│ │ │ ├── Commands
│ │ │ └── Alerts
│ │ │
│ │ └── Device
│ │
│ └── Device
│
└── Audit Logs
