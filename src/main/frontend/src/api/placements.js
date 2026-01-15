import axiosInstance from './axios';

export const placementsAPI = {
    getAll: async () => {
        const response = await axiosInstance.get('/admin/ads/placements');
        return response.data.data;
    },

    getById: async (id) => {
        const response = await axiosInstance.get(`/admin/ads/placements/${id}`);
        return response.data.data;
    },

    create: async (placementData) => {
        const response = await axiosInstance.post('/admin/ads/placements', placementData);
        return response.data.data;
    },

    update: async (id, placementData) => {
        const response = await axiosInstance.patch(`/admin/ads/placements/${id}`, placementData);
        return response.data.data;
    },

    delete: async (id) => {
        const response = await axiosInstance.delete(`/admin/ads/placements/${id}`);
        return response.data.data;
    },
};
