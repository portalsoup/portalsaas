import React from 'react'

export const Layout = ({ header, body }) => {
    return (
        <div>
            <header>{header}</header>
            <section>{body}</section>
        </div>
    )
}