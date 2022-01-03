import { forLoopTest, forOfTest, mapTest } from './src/PerformanceTesting';

import { getErrorMessage } from './src/Try-Catch-Errors';

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
