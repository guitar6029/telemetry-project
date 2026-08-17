package com.joshsoll.telemetry.platform.device.importer.dto;

import com.joshsoll.telemetry.platform.devicetemplate.entity.DeviceTemplate;
import com.joshsoll.telemetry.platform.hierarchy.entity.HierarchyNode;
import com.joshsoll.telemetry.platform.organization.entity.Organization;

public record DeviceImportContext(
        Organization organization,
        DeviceTemplate deviceTemplate,
        HierarchyNode hierarchyNode) {

}
