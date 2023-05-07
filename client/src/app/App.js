import React from 'react'
import {Provider} from 'react-redux'
import store from './store'
import {createBrowserRouter, createRoutesFromElements, Outlet, Route, RouterProvider} from "react-router-dom";
import {Counter} from "./components/counter/Counter";
import {Home} from "./components/home/Home";
import {About} from "./components/about/About";
import {Navbar} from "./components/nav/Navbar";
import "./App.scss"

export const App = () => {
    const router = createBrowserRouter(
        createRoutesFromElements(
            <Route path="/" element={<Root/>}>
                <Route index element={<Home/>}/>
                <Route path="/counter" element={<Counter/>}/>
                <Route path="/about" element={<About/>}/>
            </Route>
        )
    )

    return (
        <Provider store={store}>
            <div className="full-screen">
                <RouterProvider router={router}/>
            </div>
        </Provider>
    )
}

const Root = () => {
    return <>
        <div>
            This is the navbar
            <Navbar/>
        </div>
        <div>
            <Outlet></Outlet>
        </div>
    </>
}