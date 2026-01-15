import axiosInstance from './axios';

export const mediaAPI = {
    upload: async (file) => {
        const formData = new FormData();
        formData.append('file', file);
        const response = await axiosInstance.post('/admin/media', formData, {
            headers: {
                'Content-Type': 'multipart/form-data',
            },
        });
        return response.data;
    },

    delete: async (id) => {
        const response = await axiosInstance.delete(`/admin/media/${id}`);
        return response.data;
    },
};
