import { debounceTime, MonoTypeOperatorFunction } from 'rxjs';
import { SEARCH_DEBOUNCE_MS } from '../components/search/constants/search.constants';

export const debouncer = <T>(delayMilliseconds: number = SEARCH_DEBOUNCE_MS): MonoTypeOperatorFunction<T> =>
    debounceTime(delayMilliseconds);
