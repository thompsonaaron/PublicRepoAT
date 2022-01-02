import { forLoopTest, forOfTest, mapTest } from './features/PerformanceTesting';

import { getErrorMessage } from './features/Try-Catch-Errors';

(() => {
    console.log('Can you hear me now?!?!');
    try {
        const a = mapTest();
        const b = forOfTest();
        const c = forLoopTest();
    } catch (error) {
        console.log(getErrorMessage(error));
    }
})();
