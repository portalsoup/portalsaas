import { createSlice } from '@reduxjs/toolkit'

export const importGpxSlice = createSlice({
    name: 'importGpx',
    initialState: {
        gpx: {},
        uploading: false
    },
    reducers: {
        beginGpxUpload: (state, action) => {
            if (state.uploading === false) {
                state.uploading = true
            }
        },
        uploadGpxFile: (state, action) => {
            if (state.uploading === true) {
                state.uploading = false
            }
            state.gpx = action.payload
        },

    }
})

export const { increment, decrement } = importGpxSlice.actions

export default importGpxSlice.reducer

const uploadGpx = (file) => async dispatch => {
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

const fetchUsers = () => async dispatch => {
    dispatch(usersLoading());
    const response = await usersAPI.fetchAll()
    dispatch(usersReceived(response.data));
}