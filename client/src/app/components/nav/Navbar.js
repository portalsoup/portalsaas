import React from 'react'
import './Navbar.scss'
import { Link } from "react-router-dom";

export const Navbar = () => {
    return (
        <nav>
            <ul>
                <li>
                    <Link to="/">Home</Link>
                </li>
                <li>
                    <Link to="/counter">Counter</Link>
                </li>
            </ul> 
        </nav>
    )
}   