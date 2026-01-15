import axiosInstance from './axios';

export const categoriesAPI = {
    getAll: async () => {
        const response = await axiosInstance.get('/admin/categories/get-all');
        return response.data.data;
    },

    getAllByLang: async (lang = 'uz') => {
        const response = await axiosInstance.get('/admin/categories/get-all-by-lang', {
            params: { lang },
        });
        return response.data.data;
    },

    getById: async (id) => {
        const response = await axiosInstance.get(`/admin/categories/${id}`);
        return response.data.data;
    },

    getByIdAndLang: async (id, lang) => {
        const response = await axiosInstance.get(`/admin/categories/${id}/lang/${lang}`);
        return response.data.data;
    },

    getBySlug: async (slug, lang = 'uz') => {
        const response = await axiosInstance.get(`/admin/categories/slug/${slug}`, {
            params: { lang },
        });
        return response.data.data;
    },

    create: async (categoryData) => {
        const response = await axiosInstance.post('/admin/categories/add', categoryData);
        return response.data.data;
    },

    update: async (id, categoryData) => {
        const response = await axiosInstance.put(`/admin/categories/${id}`, categoryData);
        return response.data.data;
    },

    toggleStatus: async (id) => {
        const response = await axiosInstance.patch(`/admin/categories/${id}/toggle-status`);
        return response.data.data;
    },

    delete: async (id) => {
        const response = await axiosInstance.delete(`/admin/categories/${id}`);
        return response.data.data;
    },
};
