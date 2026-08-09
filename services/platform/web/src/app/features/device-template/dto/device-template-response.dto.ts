import { MetricDefinitionResponse } from "../../metric-definition/dto/metric-definition-response.dto";

export interface DeviceTemplateResponse {
    id: string,
    name: string;
    description: string | null;
    organizationId: string;
    archived: boolean;
    metricDefinitions: MetricDefinitionResponse[]
    createdAt: string;
    updatedAt: string;

}
