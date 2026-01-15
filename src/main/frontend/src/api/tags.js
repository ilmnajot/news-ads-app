import axiosInstance from './axios';

export const tagsAPI = {
    getAll: async (params) => {
        const response = await axiosInstance.get('/admin/tags/get-all', { params });
        return response.data.data;
    },

    getById: async (id) => {
        const response = await axiosInstance.get(`/admin/tags/${id}`);
        return response.data.data;
    },

    create: async (tagData) => {
        const response = await axiosInstance.post('/admin/tags/add', tagData);
        return response.data.data;
    },

    update: async (id, tagData) => {
        const response = await axiosInstance.patch(`/admin/tags/${id}`, tagData);
        return response.data.data;
    },

    delete: async (id) => {
        const response = await axiosInstance.delete(`/admin/tags/${id}`);
        return response.data.data;
    },
};
