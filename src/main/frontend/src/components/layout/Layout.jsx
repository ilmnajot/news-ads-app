import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import {
    Newspaper,
    FolderTree,
    Tag,
    Megaphone,
    LayoutGrid,
    LogOut,
    Menu,
    X,
    Home,
    Search,
    Bell,
    ExternalLink,
    ChevronRight,
    Shield
} from 'lucide-react';
import { useAuthStore } from '../../store/authStore';

export const Layout = ({ children }) => {
    const [sidebarOpen, setSidebarOpen] = useState(true);
    const navigate = useNavigate();
    const location = useLocation();
    const { user, logout } = useAuthStore();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    const menuItems = [
        { icon: Home, label: 'Dashboard', path: '/admin' },
        { icon: Newspaper, label: 'Articles', path: '/admin/news' },
        { icon: FolderTree, label: 'Categories', path: '/admin/categories' },
        { icon: Tag, label: 'Tags', path: '/admin/tags' },
        { icon: Megaphone, label: 'Campaigns', path: '/admin/campaigns' },
        { icon: LayoutGrid, label: 'Ad Placements', path: '/admin/assignments' },
        { icon: Shield, label: 'Users', path: '/admin/users' },
    ];

    return (
        <div className="min-h-screen bg-[#FDFDFF] flex overflow-hidden font-sans">
            {/* Sidebar */}
            <aside
                className={`fixed inset-y-0 left-0 z-50 w-72 transition-all duration-500 ease-in-out transform ${sidebarOpen ? 'translate-x-0' : '-translate-x-full'
                    } bg-white border-r border-slate-100 shadow-[20px_0_50px_-20px_rgba(0,0,0,0.02)]`}
            >
                <div className="flex flex-col h-full">
                    {/* Logo Area */}
                    <div className="p-8 pb-10 flex items-center justify-between">
                        <Link to="/admin" className="flex items-center gap-3 group">
                            <div className="w-10 h-10 bg-primary-600 rounded-2xl flex items-center justify-center text-white shadow-xl shadow-primary-500/20 group-hover:rotate-6 transition-transform">
                                <Search size={20} />
                            </div>
                            <span className="text-2xl font-black text-slate-900 tracking-tight font-heading">
                                News<span className="text-primary-600">ADS</span>
                            </span>
                        </Link>
                        <button
                            onClick={() => setSidebarOpen(false)}
                            className="lg:hidden p-2 hover:bg-slate-100 rounded-xl transition-colors"
                        >
                            <X size={20} className="text-slate-400" />
                        </button>
                    </div>

                    {/* Navigation */}
                    <nav className="flex-1 px-4 space-y-1.5 overflow-y-auto custom-scrollbar">
                        <p className="px-4 mb-4 text-[11px] font-bold text-slate-400 uppercase tracking-[2px]">Management</p>
                        {menuItems.map((item) => {
                            const Icon = item.icon;
                            const isActive = location.pathname === item.path ||
                                (item.path !== '/admin' && location.pathname.startsWith(item.path));

                            return (
                                <Link
                                    key={item.path}
                                    to={item.path}
                                    className={`relative flex items-center justify-between px-4 py-3.5 rounded-2xl transition-all duration-300 group ${isActive
                                        ? 'bg-primary-50 text-primary-700 shadow-sm'
                                        : 'text-slate-500 hover:bg-slate-50 hover:text-slate-900'
                                        }`}
                                >
                                    <div className="flex items-center gap-3.5">
                                        <div className={`p-2 rounded-xl transition-colors ${isActive ? 'bg-white shadow-sm text-primary-600' : 'bg-slate-50 text-slate-400 group-hover:bg-white group-hover:text-slate-900'
                                            }`}>
                                            <Icon size={18} />
                                        </div>
                                        <span className="font-semibold text-sm">{item.label}</span>
                                    </div>
                                    {isActive && <ChevronRight size={14} className="text-primary-400 animate-pulse" />}
                                </Link>
                            );
                        })}
                    </nav>

                    {/* User Profile Area */}
                    <div className="p-6 m-4 bg-slate-50 rounded-[2rem] border border-slate-100">
                        <div className="flex items-center gap-3 mb-4">
                            <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-white to-slate-200 border border-white shadow-sm flex items-center justify-center font-bold text-slate-700">
                                {user?.username?.charAt(0).toUpperCase() || 'A'}
                            </div>
                            <div className="flex-1 min-w-0">
                                <p className="text-sm font-bold text-slate-900 truncate">{user?.username || 'Administrator'}</p>
                                <p className="text-[10px] font-bold text-primary-600 uppercase tracking-wider">{user?.role || 'Creator'}</p>
                            </div>
                        </div>
                        <button
                            onClick={handleLogout}
                            className="w-full flex items-center justify-center gap-2 px-4 py-3 text-red-500 hover:bg-white rounded-xl transition-all duration-300 font-bold text-xs shadow-none hover:shadow-sm"
                        >
                            <LogOut size={16} />
                            Log Out
                        </button>
                    </div>
                </div>
            </aside>

            {/* Main Content Wrapper */}
            <div className={`flex-1 flex flex-col min-w-0 transition-all duration-500 ${sidebarOpen ? 'lg:ml-[288px]' : 'ml-0'}`}>
                {/* Modern Header */}
                <header className="h-20 bg-white/70 backdrop-blur-md border-b border-slate-100 sticky top-0 z-40 px-8 flex items-center justify-between">
                    <div className="flex items-center gap-4">
                        <button
                            onClick={() => setSidebarOpen(!sidebarOpen)}
                            className="p-2.5 bg-slate-50 hover:bg-slate-100 rounded-xl transition-all text-slate-600 border border-slate-100"
                        >
                            <Menu size={20} />
                        </button>
                        <div className="hidden md:flex items-center gap-2 text-sm text-slate-400 font-medium">
                            <span className="hover:text-slate-600 transition-colors">Admin</span>
                            <ChevronRight size={14} />
                            <span className="text-slate-900 font-bold capitalize">
                                {location.pathname === '/admin' ? 'Dashboard' : location.pathname.split('/')[2]?.replace('-', ' ')}
                            </span>
                        </div>
                    </div>

                    <div className="flex items-center gap-4">
                        <Link
                            to="/public"
                            target="_blank"
                            className="hidden sm:flex items-center gap-2 px-4 py-2 text-slate-500 hover:text-primary-600 bg-slate-50 hover:bg-primary-50 rounded-xl transition-all font-bold text-xs border border-slate-100"
                        >
                            <ExternalLink size={14} />
                            Live Portal
                        </Link>
                        <div className="w-px h-6 bg-slate-100 mx-1"></div>
                        <button className="relative p-2.5 text-slate-400 hover:text-primary-600 transition-colors">
                            <Bell size={20} />
                            <span className="absolute top-2 right-2 w-2 h-2 bg-primary-500 rounded-full border-2 border-white"></span>
                        </button>
                    </div>
                </header>

                {/* Content Area */}
                <main className="flex-1 overflow-y-auto p-8 lg:p-12 animate-fade-in custom-scrollbar">
                    {children}
                </main>
            </div>

            {/* Backdrop for Mobile */}
            {!sidebarOpen && (
                <div className="fixed inset-0 bg-slate-900/20 backdrop-blur-sm z-45 lg:hidden" onClick={() => setSidebarOpen(true)}></div>
            )}
        </div>
    );
};
