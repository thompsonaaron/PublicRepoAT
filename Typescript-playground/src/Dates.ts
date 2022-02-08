import { DateTime } from 'luxon';

const isoDateRegex = /^\d{4}-[01]\d-[0-3]\d$/;

export const toLocalDateString = (isoDateInput: string): string => {
    if (!isoDateRegex.test(isoDateInput)) {
        throw new Error(
            'Cannot format the provided string. Please make sure the input string is in ISO format.'
        );
    }
    return DateTime.fromISO(isoDateInput).toFormat('MM/dd/yyyy');
};
