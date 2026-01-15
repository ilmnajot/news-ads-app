import axios from './axios';

export const usersAPI = {
    getAll: (params) => axios.get('/admin/auth/get-all-users', { params }).then(res => res.data),
    register: (data) => axios.post('/admin/auth/register', data).then(res => res.data),
    update: (id, data) => axios.put(`/admin/auth/change-credentials/${id}`, data).then(res => res.data),
    toggleStatus: (id, status) => axios.put(`/admin/auth/toggle-status/${id}?status=${status}`).then(res => res.data),
    remove: (id) => axios.delete(`/admin/auth/remove-user/${id}`).then(res => res.data),
    getRoles: () => axios.get('/admin/auth/get-roles').then(res => res.data),
};
