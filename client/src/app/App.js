import React from 'react'
import { useSelector, useDispatch } from 'react-redux'
import { Counter } from "./features/counter/Counter"
import { Provider } from 'react-redux'
import store from './store'


export default function App() {
    return (
        <Provider store={store}>
            <div className="full-screen">
                <div>
                    <h1>
                        Portalsaas
                    </h1>
                    <br/>
                    <a
                        className="button-line"
                        href="https://github.com/portalsoup"
                        target="_blank"
                    >
                        Github
                    </a>
                </div>
                <Counter />
            </div>
        </Provider>
    )
}