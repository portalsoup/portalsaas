import React from 'react'

export const ImportGpx = () => {
    async function upload(file) {
        const formData = new FormData()
        formData.append('myFile', file)
        await fetch("http://localhost:8080/route/import/gpx", {
            method: "POST",
            body: formData,
            credentials: "include",
        }).then(response => {
            return response
        })
    }

    return (
        <div>
            <input type="file" onChange={upload} />
        </div>
    )
}