import { MetricDefinitionRequest } from "../../metric-definition/dto/metric-definition-request.dto";

export interface DeviceTemplateRequest {
    name: string;
    organizationId: string;
    metricDefinitions: MetricDefinitionRequest[]
}
