import { combineReducers, applyMiddleware, createStore, compose } from "redux";
import { reducer as form } from "redux-form";
import { routerReducer as routing, routerMiddleware } from "react-router-redux";

import promise from "redux-promise";
import logger from "redux-logger";

import { DEBUG } from "metabase/lib/debug";
import { logging } from "metabase/ext/middleware/logging";

/**
 * Provides the same functionality as redux-thunk and augments the dispatch method with
 * `dispatch.action(type, payload)` which creates an action that adheres to Flux Standard Action format.
 */
export const thunkWithDispatchAction = ({
  dispatch,
  getState,
}) => next => action => {
  if (typeof action === "function") {
    const dispatchAugmented = Object.assign(dispatch, {
      action: (type, payload) => dispatch({ type, payload }),
    });

    return action(dispatchAugmented, getState);
  }
  return next(action);
};

const devToolsExtension = window.__REDUX_DEVTOOLS_EXTENSION__
  ? window.__REDUX_DEVTOOLS_EXTENSION__()
  : f => f;

import { Api } from "metabase/api";
import { PLUGIN_REDUX_MIDDLEWARES } from "metabase/plugins";

export function getStore(reducers, history, intialState) {
  const reducer = combineReducers({
    ...reducers,
    routing,
    [Api.reducerPath]: Api.reducer,
    logging,
  });

  return configureStore({
    reducer,
    preloadedState: intialState,
    middleware: getDefaultMiddleware =>
      getDefaultMiddleware({
        immutableCheck: false,
        serializableCheck: false,
      }).concat([
        promise,
        Api.middleware,
        ...(history ? [routerMiddleware(history)] : []),
        ...PLUGIN_REDUX_MIDDLEWARES,
      ]),
  });
}