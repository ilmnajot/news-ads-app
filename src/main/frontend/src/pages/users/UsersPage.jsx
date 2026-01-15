import React, { useState, useEffect } from 'react';
import { Users, Mail, UserPlus, Edit, Trash2, Power, Search, RefreshCw, Shield, MoreVertical } from 'lucide-react';
import { toast } from 'react-hot-toast';
import { usersAPI } from '../../api/users';
import { Button } from '../../components/common/Button';
import { Modal } from '../../components/common/Modal';
import { Input } from '../../components/common/FormFields';

export const UsersPage = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(false);
    const [roles, setRoles] = useState([]);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [isSaving, setIsSaving] = useState(false);
    const [filters, setFilters] = useState({ page: 0, size: 10 });

    const [formData, setFormData] = useState({
        fullName: '',
        username: '',
        email: '',
        password: '',
        roleIds: [2] // Default to USER role
    });

    useEffect(() => {
        fetchUsers();
        fetchRoles();
    }, [filters]);

    const fetchRoles = async () => {
        try {
            const response = await usersAPI.getRoles();
            setRoles(response || []);
        } catch (error) {
            console.error('Error fetching roles:', error);
        }
    };

    const fetchUsers = async () => {
        setLoading(true);
        try {
            const response = await usersAPI.getAll(filters);
            setUsers(response || []);
        } catch (error) {
            console.error('Error fetching users:', error);
            toast.error('Failed to load users');
        } finally {
            setLoading(false);
        }
    };

    const handleOpenModal = (user = null) => {
        if (user) {
            setSelectedUser(user);
            setFormData({
                fullName: user.fullName || '',
                username: user.username || '',
                email: user.email || '',
                roleIds: user.roleIds || [],
            });
        } else {
            setSelectedUser(null);
            setFormData({
                fullName: '',
                username: '',
                email: '',
                password: '',
                roleIds: [2] // Default to USER
            });
        }
        setIsModalOpen(true);
    };

    const handleSaveUser = async (e) => {
        e.preventDefault();
        setIsSaving(true);
        try {
            if (selectedUser) {
                // Remove password and isActive from update request as they aren't in UpdateDto
                const { password, ...updateData } = formData;
                await usersAPI.update(selectedUser.id, updateData);
                toast.success('User updated successfully');
            } else {
                await usersAPI.register(formData);
                toast.success('User registered successfully');
            }
            setIsModalOpen(false);
            fetchUsers();
        } catch (error) {
            console.error('Error saving user:', error);
            toast.error(error.response?.data?.message || 'Failed to save user');
        } finally {
            setIsSaving(false);
        }
    };

    const handleToggleStatus = async (user) => {
        try {
            await usersAPI.toggleStatus(user.id, !user.isActive);
            toast.success(`User ${!user.isActive ? 'activated' : 'deactivated'}`);
            fetchUsers();
        } catch (error) {
            console.error('Error toggling status:', error);
            toast.error('Failed to update user status');
        }
    };

    const handleDeleteUser = async () => {
        if (!selectedUser) return;
        try {
            await usersAPI.remove(selectedUser.id);
            toast.success('User removed successfully');
            setIsDeleteModalOpen(false);
            fetchUsers();
        } catch (error) {
            console.error('Error deleting user:', error);
            toast.error('Failed to remove user');
        }
    };

    return (
        <div className="space-y-8 animate-fade-in">
            {/* Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                    <div className="flex items-center gap-2 mb-2">
                        <div className="p-1.5 bg-primary-100 text-primary-600 rounded-lg">
                            <Shield size={14} />
                        </div>
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Security</span>
                    </div>
                    <h1 className="text-4xl font-black text-slate-900 tracking-tight font-heading">
                        Manage <span className="text-primary-600">Users</span>
                    </h1>
                </div>

                <Button variant="primary" size="lg" onClick={() => handleOpenModal()} className="shadow-2xl shadow-primary-500/30 font-bold">
                    <UserPlus size={20} className="mr-2" />
                    Add User
                </Button>
            </div>

            {/* Table Container */}
            <div className="bg-white rounded-[2rem] border border-slate-100 shadow-sm overflow-hidden min-h-[400px]">
                <div className="overflow-x-auto">
                    <table className="w-full border-collapse">
                        <thead>
                            <tr className="bg-slate-50/50 border-b border-slate-100">
                                <th className="px-8 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Full Name</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Username</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Email</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Roles</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Status</th>
                                <th className="px-8 py-5 text-right text-[10px] font-bold uppercase tracking-wider text-slate-400">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-50">
                            {loading && users.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="px-8 py-20 text-center">
                                        <RefreshCw size={40} className="animate-spin text-primary-200 mx-auto mb-4" />
                                        <p className="text-slate-400 font-bold">Loading users...</p>
                                    </td>
                                </tr>
                            ) : users.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="px-8 py-20 text-center">
                                        <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-300">
                                            <Users size={32} />
                                        </div>
                                        <p className="text-slate-400 font-bold">No users found</p>
                                    </td>
                                </tr>
                            ) : (
                                users.map((user) => (
                                    <tr key={user.id} className="group hover:bg-slate-50/50 transition-colors">
                                        <td className="px-8 py-5">
                                            <div className="flex items-center gap-3">
                                                <div className="w-10 h-10 rounded-xl bg-primary-50 flex items-center justify-center text-primary-600 font-black text-sm group-hover:scale-110 transition-transform">
                                                    {user.fullName?.[0] || user.username?.[0]}
                                                </div>
                                                <div className="text-sm font-bold text-slate-900">{user.fullName}</div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-5 text-sm font-medium text-slate-600">@{user.username}</td>
                                        <td className="px-6 py-5 text-sm font-medium text-slate-600">{user.email}</td>
                                        <td className="px-6 py-5">
                                            <div className="flex flex-wrap gap-1">
                                                {(user.roleNames || []).map((role, idx) => (
                                                    <span key={idx} className="px-2 py-0.5 bg-slate-100 text-slate-600 rounded text-[9px] font-bold uppercase">
                                                        {role.replace('ROLE_', '')}
                                                    </span>
                                                ))}
                                            </div>
                                        </td>
                                        <td className="px-6 py-5">
                                            <button
                                                onClick={() => handleToggleStatus(user)}
                                                className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider border transition-all ${user.isActive
                                                    ? 'bg-emerald-50 text-emerald-600 border-emerald-100 hover:bg-emerald-100'
                                                    : 'bg-slate-50 text-slate-400 border-slate-100 hover:bg-slate-100'
                                                    }`}
                                            >
                                                {user.isActive ? 'Active' : 'Inactive'}
                                            </button>
                                        </td>
                                        <td className="px-8 py-5 text-right">
                                            <div className="flex items-center justify-end gap-2">
                                                <button
                                                    onClick={() => handleOpenModal(user)}
                                                    className="p-2 text-slate-400 hover:text-primary-600 hover:bg-primary-50 rounded-lg transition-all"
                                                >
                                                    <Edit size={16} />
                                                </button>
                                                <button
                                                    onClick={() => {
                                                        setSelectedUser(user);
                                                        setIsDeleteModalOpen(true);
                                                    }}
                                                    className="p-2 text-slate-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-all"
                                                >
                                                    <Trash2 size={16} />
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Modal for Add/Edit */}
            <Modal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                title={selectedUser ? 'Edit User Credentials' : 'Register New User'}
            >
                <form onSubmit={handleSaveUser} className="space-y-6">
                    <Input
                        label="Full Name"
                        value={formData.fullName}
                        onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                        placeholder="John Doe"
                        required
                    />
                    <div className="grid grid-cols-2 gap-4">
                        <Input
                            label="Username"
                            value={formData.username}
                            onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                            placeholder="johndoe"
                            required
                        />
                        <Input
                            label="Email Address"
                            type="email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            placeholder="john@example.com"
                            required
                        />
                    </div>

                    <div className="space-y-2">
                        <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider ml-1">Assign Roles</label>
                        <div className="flex flex-wrap gap-2 p-4 bg-slate-50 rounded-2xl border border-slate-100">
                            {roles.map((role) => (
                                <button
                                    key={role.id}
                                    type="button"
                                    onClick={() => {
                                        const currentRoles = formData.roleIds || [];
                                        const newRoles = currentRoles.includes(role.id)
                                            ? currentRoles.filter(id => id !== role.id)
                                            : [...currentRoles, role.id];
                                        setFormData({ ...formData, roleIds: newRoles });
                                    }}
                                    className={`px-3 py-1.5 rounded-xl text-[10px] font-bold uppercase transition-all border ${formData.roleIds?.includes(role.id)
                                        ? 'bg-primary-600 text-white border-primary-600 shadow-lg shadow-primary-500/30'
                                        : 'bg-white text-slate-400 border-slate-200 hover:border-primary-300'
                                        }`}
                                >
                                    {role.name.replace('ROLE_', '')}
                                </button>
                            ))}
                        </div>
                    </div>

                    {!selectedUser && (
                        <Input
                            label="Set Password"
                            type="password"
                            value={formData.password}
                            onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                            placeholder="Minimum 6 characters"
                            required
                        />
                    )}

                    <div className="flex gap-3 pt-4">
                        <Button
                            type="button"
                            variant="secondary"
                            onClick={() => setIsModalOpen(false)}
                            className="flex-1"
                        >
                            Cancel
                        </Button>
                        <Button
                            type="submit"
                            variant="primary"
                            loading={isSaving}
                            className="flex-1 font-bold"
                        >
                            {selectedUser ? 'Update User' : 'Register User'}
                        </Button>
                    </div>
                </form>
            </Modal>

            {/* Delete Confirmation Modal */}
            <Modal
                isOpen={isDeleteModalOpen}
                onClose={() => setIsDeleteModalOpen(false)}
                title="Remove User"
            >
                <div className="space-y-6">
                    <p className="text-slate-600">
                        Are you sure you want to remove <span className="font-bold text-slate-900">@{selectedUser?.username}</span> from the system? This action cannot be undone.
                    </p>
                    <div className="flex gap-3">
                        <Button
                            variant="secondary"
                            onClick={() => setIsDeleteModalOpen(false)}
                            className="flex-1"
                        >
                            Cancel
                        </Button>
                        <Button
                            variant="danger"
                            onClick={handleDeleteUser}
                            className="flex-1 font-bold"
                        >
                            Yes, Remove User
                        </Button>
                    </div>
                </div>
            </Modal>
        </div>
    );
};
