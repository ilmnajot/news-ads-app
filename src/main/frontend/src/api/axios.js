import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1';

const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Request interceptor to add auth token
axiosInstance.interceptors.request.use(
    (config) => {
        // Detect public endpoints more robustly
        const url = config.url || '';
        const isPublicAuthRequest = url.includes('/auth/login') || url.includes('/auth/refresh');
        const isPublicRequest = url.includes('/public/');

        console.log(`[Request] ${config.method?.toUpperCase()} ${url}`, { isPublicAuthRequest, isPublicRequest });

        if (!isPublicAuthRequest && !isPublicRequest) {
            const token = localStorage.getItem('accessToken');
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
                // console.log(`[Auth] Token attached to ${url}`);
            } else {
                console.warn(`[Auth] No token found in localStorage for ${url}`);
            }
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Response interceptor for token refresh
axiosInstance.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // If 401 and not already retried, try to refresh token
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;

            try {
                const refreshToken = localStorage.getItem('refreshToken');
                if (refreshToken) {
                    const response = await axios.post(
                        `${API_BASE_URL}/admin/auth/refresh`,
                        null,
                        { params: { refreshToken } }
                    );

                    const { accessToken, refreshToken: newRefreshToken } = response.data.data;
                    localStorage.setItem('accessToken', accessToken);
                    localStorage.setItem('refreshToken', newRefreshToken);

                    originalRequest.headers.Authorization = `Bearer ${accessToken}`;
                    return axiosInstance(originalRequest);
                }
            } catch (refreshError) {
                // Refresh failed, logout user
                localStorage.removeItem('accessToken');
                localStorage.removeItem('refreshToken');
                localStorage.removeItem('user');
                window.location.href = '/login';
                return Promise.reject(refreshError);
            }
        }

        return Promise.reject(error);
    }
);

export default axiosInstance;
