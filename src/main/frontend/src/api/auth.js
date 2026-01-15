import axiosInstance from './axios';

export const authAPI = {
    login: async (credentials) => {
        const response = await axiosInstance.post('/admin/auth/login', credentials);
        return response.data.data;
    },

    register: async (userData) => {
        const response = await axiosInstance.post('/admin/auth/register', userData);
        return response.data.data;
    },

    refreshToken: async (refreshToken) => {
        const response = await axiosInstance.post('/admin/auth/refresh', null, {
            params: { refreshToken },
        });
        return response.data.data;
    },

    logout: async () => {
        const response = await axiosInstance.post('/admin/auth/logout');
        return response.data;
    },
};
