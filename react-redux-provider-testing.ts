// eslint-disable-next-line import/named
import { Dispatch, PreloadedState, applyMiddleware, configureStore } from '@reduxjs/toolkit';
import React, { PropsWithChildren } from 'react';

import AxiosMiddleware from '../redux/middleware/AxiosMiddleware';
// eslint-disable-next-line import/named
import { Provider } from 'react-redux';
import type { RenderOptions } from '@testing-library/react';
import { RootState } from '../hooks/useAppDispatch';
import combinedReducers from '../redux/reducers/index';
import { oktaAuth } from '../redux/store';
import promiseMiddleware from 'redux-promise-middleware';
import { render } from '@testing-library/react';

// This type interface extends the default options for render from RTL, as well
// as allows the user to specify other things such as initialState, store.
type AppStore = ReturnType<typeof setupStore>;

interface ExtendedRenderOptions extends Omit<RenderOptions, 'queries'> {
    preloadedState?: PreloadedState<RootState>;
    store?: AppStore;
}

export type TestStore = AppStore & { dispatch: jest.Mock<AppStore['dispatch']> };

function setupStore(preloadedState?: PreloadedState<RootState>) {
    return configureStore({
        reducer: combinedReducers,
        middleware: (getDefaultMiddleware) =>
            getDefaultMiddleware({
                serializableCheck: false, // for some reason redux thinks our values are not-serializable and throws warning
                thunk: {
                    extraArgument: {
                        api: new AxiosMiddleware(oktaAuth),
                    },
                },
            }),
        enhancers: [applyMiddleware(promiseMiddleware)],
        preloadedState,
    });
}

/** Mocks the dispatch function so we can test actual calls like so:
 * expect(store.dispatch).toHaveBeenCalledWith(actionCreatorFunction) */
export function makeTestStore(preloadedState?: PreloadedState<RootState>): TestStore {
    const store = setupStore(preloadedState);
    const origDispatch = store.dispatch;
    store.dispatch = jest.fn(origDispatch) as Dispatch;
    return store as TestStore;
}

/** Refer to https://redux.js.org/usage/writing-tests */
export function renderWithProviders(
    ui: React.ReactElement,
    {
        preloadedState = {},
        // Automatically create a store instance if no store was passed in
        store = makeTestStore(preloadedState),
        // store = configureStore({ reducer: combinedReducers, preloadedState }),
        ...renderOptions
    }: ExtendedRenderOptions = {}
) {
    // eslint-disable-next-line @typescript-eslint/ban-types
    function Wrapper({ children }: PropsWithChildren<{}>): JSX.Element {
        return <Provider store={store}>{children}</Provider>;
    }

    // Return an object with the store and all of RTL's query functions
    return { store, ...render(ui, { wrapper: Wrapper, ...renderOptions }) };
}
