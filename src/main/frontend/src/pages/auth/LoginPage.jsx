import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { LogIn, Sparkles, User, Lock } from 'lucide-react';
import toast from 'react-hot-toast';
import { authAPI } from '../../api/auth';
import { useAuthStore } from '../../store/authStore';
import { Button } from '../../components/common/Button';

export const LoginPage = () => {
    const navigate = useNavigate();
    const setAuth = useAuthStore((state) => state.setAuth);
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({
        username: '',
        password: '',
    });

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(false); // Reset loading state for clean start
        setLoading(true);

        try {
            const response = await authAPI.login(formData);

            // Extract user info from response
            const user = {
                username: formData.username,
                role: 'ADMIN', // Defaulting to ADMIN as this is admin login
            };

            setAuth(user, response.accessToken, response.refreshToken);
            toast.success('Welcome back, ' + formData.username + '!');
            navigate('/admin');
        } catch (error) {
            console.error('Login error:', error);
            // Check for specific error messages or network errors
            if (error.code === 'ERR_NETWORK') {
                toast.error('Network error. Is the backend server running?');
            } else {
                toast.error(error.response?.data?.message || 'Authentication failed. Please check your credentials.');
            }
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    return (
        <div className="min-h-screen relative flex items-center justify-center overflow-hidden bg-slate-950 font-sans">
            {/* Dynamic Background Elements */}
            <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-primary-600/20 rounded-full blur-[120px] animate-pulse"></div>
            <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-indigo-600/20 rounded-full blur-[120px] animate-pulse" style={{ animationDelay: '1s' }}></div>

            {/* Grid Pattern */}
            <div className="absolute inset-0 bg-[url('https://grainy-gradients.vercel.app/noise.svg')] opacity-20 brightness-100 contrast-150"></div>
            <div className="absolute inset-0 bg-grid-slate-900/[0.04] bg-[bottom_1px_center] mask-image:linear-gradient(to_bottom,transparent,black)"></div>

            <div className="relative z-10 w-full max-w-md px-6 animate-fade-in">
                {/* Brand / Logo */}
                <div className="text-center mb-10">
                    <div className="inline-flex items-center justify-center p-4 bg-gradient-to-tr from-primary-600 to-indigo-500 rounded-3xl shadow-2xl shadow-primary-500/20 mb-6 group transition-transform hover:scale-110">
                        <Sparkles className="text-white group-hover:rotate-12 transition-transform" size={40} />
                    </div>
                    <h1 className="text-4xl font-extrabold text-white tracking-tight font-heading">
                        Pulse<span className="text-primary-400">News</span>
                    </h1>
                    <p className="text-slate-400 mt-3 text-lg font-medium">Elevate Your Content Management</p>
                </div>

                {/* Login Card */}
                <div className="glass rounded-[2rem] p-1 shadow-2xl">
                    <div className="bg-white/80 dark:bg-slate-900/40 rounded-[1.9rem] p-8 md:p-10 backdrop-blur-xl border border-white/20">
                        <form onSubmit={handleSubmit} className="space-y-6">
                            <div className="space-y-2">
                                <label className="text-sm font-semibold text-slate-700 dark:text-slate-300 ml-1">Username</label>
                                <div className="relative group">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <User size={18} className="text-slate-400 group-focus-within:text-primary-500 transition-colors" />
                                    </div>
                                    <input
                                        name="username"
                                        type="text"
                                        value={formData.username}
                                        onChange={handleChange}
                                        className="w-full pl-11 pr-4 py-3.5 bg-slate-50/50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all text-slate-900 font-medium"
                                        placeholder="admin"
                                        required
                                    />
                                </div>
                            </div>

                            <div className="space-y-2">
                                <label className="text-sm font-semibold text-slate-700 dark:text-slate-300 ml-1">Password</label>
                                <div className="relative group">
                                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                                        <Lock size={18} className="text-slate-400 group-focus-within:text-primary-500 transition-colors" />
                                    </div>
                                    <input
                                        name="password"
                                        type="password"
                                        value={formData.password}
                                        onChange={handleChange}
                                        className="w-full pl-11 pr-4 py-3.5 bg-slate-50/50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-4 focus:ring-primary-500/10 focus:border-primary-500 transition-all text-slate-900 font-medium"
                                        placeholder="••••••••"
                                        required
                                    />
                                </div>
                            </div>

                            <button
                                type="submit"
                                disabled={loading}
                                className="w-full btn-premium py-4"
                            >
                                <div className="flex items-center justify-center">
                                    {loading ? (
                                        <div className="flex items-center gap-2">
                                            <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                                            <span>Authenticating...</span>
                                        </div>
                                    ) : (
                                        <span className="flex items-center gap-2">
                                            Sign In <LogIn size={18} />
                                        </span>
                                    )}
                                </div>
                            </button>
                        </form>

                        <div className="mt-8 pt-8 border-t border-slate-200/50 text-center">
                            <Link to="/public" className="text-slate-500 hover:text-primary-600 transition-colors font-medium text-sm inline-flex items-center gap-1 group">
                                ← Back to Public Portal
                                <span className="w-0 group-hover:w-full transition-all duration-300 h-0.5 bg-primary-600 absolute bottom-0 left-0"></span>
                            </Link>
                        </div>
                    </div>
                </div>

                {/* Status Help */}
                <div className="mt-8 text-center animate-fade-in" style={{ animationDelay: '0.2s' }}>
                    <p className="text-slate-500 text-sm">
                        Default access: <span className="text-slate-300 font-semibold">admin / admin</span>
                    </p>
                </div>
            </div>
        </div>
    );
};
