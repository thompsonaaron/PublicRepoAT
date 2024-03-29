import { getErrorMessage, isErrorWithMessage, toErrorWithMessage } from './Try-Catch-Errors';

const obj = { a: null };
obj.a = { b: obj };

describe('try-catch-errors tests', () => {
    it('error -> true', () => {
        expect(isErrorWithMessage(new Error('test error'))).toBe(true);
    });

    it('error -> false', () => {
        expect(isErrorWithMessage('definitely not an error')).toBe(false);
    });

    // json.stringify throws an error with circular object references
    it('toErrorWithMessage json.stringify failure', () => {
        expect(toErrorWithMessage(obj)).toEqual(Error(String(obj)));
    });

    it('getErrorMessage integration golden path integration test', () => {
        expect(getErrorMessage(new Error('test error message'))).toEqual('test error message');
    });

    it('getErrorMessage integration failure integration test', () => {
        expect(getErrorMessage(2)).toEqual('2');
    });
});
