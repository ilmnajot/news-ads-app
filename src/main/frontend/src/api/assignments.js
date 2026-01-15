import axiosInstance from './axios';

export const assignmentsAPI = {
    getAll: async () => {
        const response = await axiosInstance.get('/admin/ads/assignments/get-all');
        return response.data.data;
    },

    getById: async (id) => {
        const response = await axiosInstance.get(`/admin/ads/assignments/${id}`);
        return response.data.data;
    },

    create: async (assignmentData) => {
        const response = await axiosInstance.post('/admin/ads/assignments/add', assignmentData);
        return response.data.data;
    },

    update: async (id, assignmentData) => {
        const response = await axiosInstance.put(`/admin/ads/assignments/${id}`, assignmentData);
        return response.data.data;
    },

    delete: async (id) => {
        const response = await axiosInstance.delete(`/admin/ads/assignments/${id}`);
        return response.data.data;
    },
};

export const publicAdsAPI = {
    getAd: async (placementCode, lang = 'uz', categoryId = null) => {
        const response = await axiosInstance.get(`/public/ads/${placementCode}`, {
            params: { lang, categoryId },
        });
        return response.data.data;
    },
};
