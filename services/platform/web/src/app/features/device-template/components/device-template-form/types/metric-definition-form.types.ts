import { FormControl, FormGroup } from "@angular/forms";
import { MetricDataType } from "../../../../metric-definition/enums/metric-data-type.enum";

export type MetricDefinitionForm = FormGroup<{
    id: FormControl<string | null>;
    name: FormControl<string>;
    incomingFieldName: FormControl<string>;
    dataType: FormControl<MetricDataType>;
    description: FormControl<string>;
    unit: FormControl<string>;
}>;
