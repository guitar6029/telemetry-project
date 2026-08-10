import { MetricDefinitionRequest } from "../../metric-definition/dto/metric-definition-request.dto";

export interface DeviceTemplateRequest {
    name: string;
    description: string | null;
    metricDefinitions: MetricDefinitionRequest[]
}
