import { equals } from 'ramda';

export const isEqual = <T extends string | undefined>(
  a: T | T[],
  b: T | T[],
  caseInsensitive = false
): boolean => {
  if (typeof a === 'string' && typeof b === 'string') {
    return caseInsensitive
      ? a.localeCompare(b, undefined, { sensitivity: 'accent' }) === 0
      : a === b;
  }

  if (Array.isArray(a) && Array.isArray(b)) {
    return a.length === b.length && a.every((element) => b.includes(element));
  }

  return equals(a, b);
};
