import { MetricDefinitionUpdateRequest } from "../../metric-definition/dto/metric-definition-update-request.dto";

export interface DeviceTemplateUpdateRequest {
    name: string;
    description: string | null;
    metricDefinitions: MetricDefinitionUpdateRequest[]
}

