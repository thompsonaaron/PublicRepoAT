import { getErrorMessage } from './features/Try-Catch-Errors';

(() => {
    console.log('Can you hear me now?!?!');

    try {
        throw true;
    } catch (error) {
        console.log(getErrorMessage(error));
    }
})();
