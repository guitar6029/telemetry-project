import { MetricDataType } from "../enums/metric-data-type.enum";

export interface MetricDefinitionResponse {
    id: string;
    name: string;
    incomingFieldName: string;
    dataType: MetricDataType;
    description: string | null;
    unit: string | null;
    deviceTemplateId: string;
    createdAt: string;
    updatedAt: string;
}
