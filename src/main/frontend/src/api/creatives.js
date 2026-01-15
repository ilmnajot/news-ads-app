import axiosInstance from './axios';

export const creativesAPI = {
    getAll: async () => {
        const response = await axiosInstance.get('/admin/ads/creatives');
        return response.data;
    },

    getById: async (id) => {
        const response = await axiosInstance.get(`/admin/ads/creatives/${id}`);
        return response.data;
    },

    create: async (creativeData) => {
        const response = await axiosInstance.post('/admin/ads/creatives', creativeData);
        return response.data;
    },

    update: async (id, creativeData) => {
        const response = await axiosInstance.put(`/admin/ads/creatives/${id}`, creativeData);
        return response.data;
    },

    delete: async (id) => {
        const response = await axiosInstance.delete(`/admin/ads/creatives/${id}`);
        return response.data;
    },
};
