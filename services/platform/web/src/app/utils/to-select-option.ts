import { SelectOption } from "../components/select/types/select-option.types";

export function toSelectOptions<T>(
    data: T[],
    label: (item: T) => string,
    value: (item: T) => string
): SelectOption[] {
    return data.map(item => ({
        label: label(item),
        value: value(item)
    }));
}
