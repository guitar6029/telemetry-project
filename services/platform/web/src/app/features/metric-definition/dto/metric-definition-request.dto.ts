import { MetricDataType } from "../enums/metric-data-type.enum";

export interface MetricDefinitionRequest {
    name: string;
    incomingFieldName: string;
    dataType: MetricDataType;
    description: string | null;
    unit: string | null;
}
