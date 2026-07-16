# Spring Data Repository Naming

Spring Data derives queries from entity properties.

Entity

MetricDefinition

@ManyToOne
private DeviceTemplate deviceTemplate;

Correct

findAllByDeviceTemplate(...)

findAllByDeviceTemplate_Id(...)

Incorrect

findAllByDeviceTemplateId(...)

Reason

deviceTemplateId is not an entity property.

Spring traverses entity relationships, not Java helper methods.
