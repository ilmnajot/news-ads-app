import axiosInstance from './axios';

export const campaignsAPI = {
    getAll: async () => {
        const response = await axiosInstance.get('/admin/ads/campaigns');
        return response.data.data;
    },

    getById: async (id) => {
        const response = await axiosInstance.get(`/admin/ads/campaigns/${id}`);
        return response.data.data;
    },

    create: async (campaignData) => {
        const response = await axiosInstance.post('/admin/ads/campaigns', campaignData);
        return response.data.data;
    },

    update: async (id, campaignData) => {
        const response = await axiosInstance.put(`/admin/ads/campaigns/${id}`, campaignData);
        return response.data.data;
    },

    updateStatus: async (id, statusData) => {
        const response = await axiosInstance.patch(`/admin/ads/campaigns/${id}/status`, statusData);
        return response.data.data;
    },

    delete: async (id) => {
        const response = await axiosInstance.delete(`/admin/ads/campaigns/${id}`);
        return response.data.data;
    },
};
