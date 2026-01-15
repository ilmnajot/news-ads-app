import axiosInstance from './axios';

export const newsAPI = {
    getAll: async (params) => {
        const response = await axiosInstance.get('/admin/news/get-all', { params });
        return response.data.data;
    },

    getById: async (id) => {
        const response = await axiosInstance.get(`/admin/news/${id}`);
        return response.data.data;
    },

    create: async (newsData) => {
        const response = await axiosInstance.post('/admin/news/add', newsData);
        return response.data.data;
    },

    updateStatus: async (id, status) => {
        const response = await axiosInstance.patch(`/admin/news/${id}/status`, null, {
            params: { status },
        });
        return response.data.data;
    },

    softDelete: async (id) => {
        const response = await axiosInstance.delete(`/admin/news/delete/${id}`);
        return response.data.data;
    },

    restore: async (id) => {
        const response = await axiosInstance.post(`/admin/news/${id}/restore`);
        return response.data.data;
    },

    hardDelete: async (id) => {
        const response = await axiosInstance.delete(`/admin/news/${id}/hard`);
        return response.data.data;
    },

    getHistory: async (id) => {
        const response = await axiosInstance.get(`/admin/news/${id}/history`);
        return response.data.data;
    },
};

export const publicNewsAPI = {
    getAll: async (params) => {
        const response = await axiosInstance.get('/public/news', { params });
        return response.data.data;
    },

    getBySlug: async (slug, lang = 'uz') => {
        const response = await axiosInstance.get(`/public/news/${slug}`, {
            params: { lang },
        });
        return response.data.data;
    },

    getCategories: async (lang = 'uz') => {
        const response = await axiosInstance.get('/public/news/categories', {
            params: { lang },
        });
        return response.data.data;
    },

    getTags: async () => {
        const response = await axiosInstance.get('/public/news/tags');
        return response.data.data;
    },
};
