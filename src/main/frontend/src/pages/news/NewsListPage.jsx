import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
    Plus,
    Search,
    Filter,
    Eye,
    Edit,
    Trash2,
    RefreshCw,
    ArrowRight,
    MoreHorizontal,
    FileText,
    Activity,
    Calendar,
    ChevronLeft,
    ChevronRight,
    Star
} from 'lucide-react';
import toast from 'react-hot-toast';
import { newsAPI } from '../../api/news';
import { Button } from '../../components/common/Button';
import { Card } from '../../components/common/Card';
import { PageLoader } from '../../components/common/Loading';
import { ConfirmModal } from '../../components/common/Modal';

export const NewsListPage = () => {
    const [news, setNews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [deleteModal, setDeleteModal] = useState({ isOpen: false, newsId: null });
    const [filters, setFilters] = useState({
        keyword: '',
        lang: 'uz',
        status: '',
        page: 0,
        size: 10,
    });

    useEffect(() => {
        fetchNews();
    }, [filters]);

    const fetchNews = async () => {
        try {
            setLoading(true);
            const response = await newsAPI.getAll(filters);
            setNews(response || []);
        } catch (error) {
            console.error('Error fetching news:', error);
            toast.error('Failed to load articles');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        try {
            await newsAPI.softDelete(deleteModal.newsId);
            toast.success('Article moved to trash');
            setDeleteModal({ isOpen: false, newsId: null });
            fetchNews();
        } catch (error) {
            console.error('Error deleting news:', error);
            toast.error('Failed to move article to trash');
        }
    };

    const handleRestore = async (id) => {
        try {
            await newsAPI.restore(id);
            toast.success('Article restored');
            fetchNews();
        } catch (error) {
            console.error('Error restoring news:', error);
            toast.error('Failed to restore article');
        }
    };

    const getStatusStyles = (status) => {
        const styles = {
            DRAFT: 'bg-slate-100 text-slate-600 border-slate-200',
            REVIEW: 'bg-amber-50 text-amber-600 border-amber-100',
            PUBLISHED: 'bg-emerald-50 text-emerald-600 border-emerald-100',
            UNPUBLISHED: 'bg-rose-50 text-rose-600 border-rose-100',
            ARCHIVED: 'bg-indigo-50 text-indigo-600 border-indigo-100',
        };
        return styles[status] || styles.DRAFT;
    };

    return (
        <div className="space-y-10 animate-fade-in">
            {/* Page Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                    <div className="flex items-center gap-2 mb-2">
                        <div className="p-1.5 bg-primary-100 text-primary-600 rounded-lg">
                            <Activity size={14} />
                        </div>
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Content</span>
                    </div>
                    <h1 className="text-4xl font-black text-slate-900 tracking-tight font-heading">
                        Manage <span className="text-primary-600">Articles</span>
                    </h1>
                </div>

                <Link to="/admin/news/create">
                    <Button variant="primary" size="lg" className="shadow-2xl shadow-primary-500/30 font-bold">
                        <Plus size={20} className="mr-2" />
                        Add Article
                    </Button>
                </Link>
            </div>

            {/* Smart Filters Bar */}
            <div className="bg-white rounded-[2rem] p-4 border border-slate-100 shadow-xl shadow-slate-200/40 flex flex-col lg:flex-row items-center gap-4">
                <div className="relative flex-1 w-full group">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                        <Search size={18} className="text-slate-400 group-focus-within:text-primary-500 transition-colors" />
                    </div>
                    <input
                        type="text"
                        placeholder="Search articles..."
                        className="w-full pl-11 pr-4 py-3 bg-slate-50 border-transparent rounded-[1.2rem] focus:outline-none focus:ring-4 focus:ring-primary-100 focus:bg-white transition-all text-sm font-medium"
                        value={filters.keyword}
                        onChange={(e) => setFilters({ ...filters, keyword: e.target.value })}
                    />
                </div>

                <div className="flex items-center gap-3 w-full lg:w-auto">
                    <select
                        className="flex-1 lg:w-40 px-4 py-3 bg-slate-50 border-none rounded-[1.5rem] text-sm font-bold text-slate-600 focus:ring-4 focus:ring-primary-100 transition-all cursor-pointer appearance-none"
                        value={filters.lang}
                        onChange={(e) => setFilters({ ...filters, lang: e.target.value })}
                    >
                        <option value="uz">Uzbek</option>
                        <option value="ru">Russian</option>
                        <option value="en">English</option>
                    </select>

                    <select
                        className="flex-1 lg:w-40 px-4 py-3 bg-slate-50 border-none rounded-[1.5rem] text-sm font-bold text-slate-600 focus:ring-4 focus:ring-primary-100 transition-all cursor-pointer appearance-none"
                        value={filters.status}
                        onChange={(e) => setFilters({ ...filters, status: e.target.value })}
                    >
                        <option value="">All Status</option>
                        <option value="DRAFT">Draft</option>
                        <option value="REVIEW">Review</option>
                        <option value="PUBLISHED">Published</option>
                        <option value="ARCHIVED">Archived</option>
                    </select>

                    <button
                        onClick={fetchNews}
                        className="p-3 bg-slate-900 text-white rounded-[1.2rem] hover:bg-slate-800 transition-all shadow-lg active:scale-95"
                    >
                        <RefreshCw size={20} className={loading ? 'animate-spin' : ''} />
                    </button>
                </div>
            </div>

            {/* Premium Table Content */}
            <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-[0_20px_60px_-15px_rgba(0,0,0,0.03)] overflow-hidden">
                <div className="overflow-x-auto custom-scrollbar">
                    <table className="w-full border-collapse">
                        <thead>
                            <tr className="bg-slate-50/50 border-b border-slate-100">
                                <thead>
                                    <tr className="bg-slate-50/50 border-b border-slate-100">
                                        <th className="px-8 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Article Title</th>
                                        <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Category</th>
                                        <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Status</th>
                                        <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Date</th>
                                        <th className="px-8 py-5 text-right text-[10px] font-bold uppercase tracking-wider text-slate-400">Actions</th>
                                    </tr>
                                </thead>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-50">
                            {loading && news.length === 0 ? (
                                <tr><td colSpan="5" className="p-32 text-center"><PageLoader /></td></tr>
                            ) : news.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="p-32 text-center">
                                        <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-300">
                                            <FileText size={32} />
                                        </div>
                                        <p className="text-slate-400 font-bold tracking-tight">No articles found</p>
                                    </td>
                                </tr>
                            ) : (
                                news.map((item) => (
                                    <tr key={item.id} className="group hover:bg-slate-50/50 transition-colors">
                                        <td className="px-8 py-6">
                                            <div className="flex items-center gap-4">
                                                <div className="w-12 h-12 rounded-2xl bg-slate-100 flex items-center justify-center text-slate-400 group-hover:scale-110 group-hover:bg-primary-50 group-hover:text-primary-600 transition-all duration-300">
                                                    <FileText size={20} />
                                                </div>
                                                <div>
                                                    <div className="text-sm font-bold text-slate-900 line-clamp-1 mb-1 group-hover:text-primary-600 transition-colors">
                                                        {item.translations?.[0]?.title || 'Untitled Article'}
                                                    </div>
                                                    <div className="flex items-center gap-2">
                                                        {item.isFeatured && (
                                                            <span className="flex items-center gap-1.5 px-2 py-0.5 bg-amber-50 text-amber-600 rounded-md text-[9px] font-bold uppercase tracking-wider border border-amber-100">
                                                                <Star size={10} fill="currentColor" /> Featured
                                                            </span>
                                                        )}
                                                        <span className="text-[10px] font-medium text-slate-400">ID: #{item.id}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-6">
                                            <div className="flex flex-col gap-1">
                                                <span className="text-xs font-bold text-slate-600">{item.category?.name || 'Uncategorized'}</span>
                                                <span className="text-[10px] font-medium text-slate-400 flex items-center gap-1">
                                                    <Activity size={10} /> {item.author?.username || 'System'}
                                                </span>
                                            </div>
                                        </td>
                                        <td className="px-6 py-6">
                                            <span className={`px-4 py-1.5 rounded-full text-[10px] font-bold uppercase tracking-wider border ${getStatusStyles(item.status)}`}>
                                                {item.status}
                                            </span>
                                        </td>
                                        <td className="px-6 py-6">
                                            <div className="flex items-center text-[10px] font-bold text-slate-400 uppercase tracking-wider">
                                                <Calendar size={12} className="mr-1.5" />
                                                {new Date(item.createdAt).toLocaleDateString()}
                                            </div>
                                        </td>
                                        <td className="px-8 py-6">
                                            <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                                                <Link to={`/admin/news/${item.id}`}>
                                                    <button className="p-2.5 bg-white border border-slate-100 text-slate-400 hover:text-primary-600 hover:border-primary-100 rounded-xl transition-all shadow-sm">
                                                        <Eye size={16} />
                                                    </button>
                                                </Link>
                                                <Link to={`/admin/news/${item.id}/edit`}>
                                                    <button className="p-2.5 bg-white border border-slate-100 text-slate-400 hover:text-indigo-600 hover:border-indigo-100 rounded-xl transition-all shadow-sm">
                                                        <Edit size={16} />
                                                    </button>
                                                </Link>
                                                {item.isDeleted ? (
                                                    <button
                                                        onClick={() => handleRestore(item.id)}
                                                        className="p-2.5 bg-white border border-slate-100 text-emerald-500 hover:bg-emerald-50 hover:border-emerald-200 rounded-xl transition-all shadow-sm"
                                                    >
                                                        <RefreshCw size={16} />
                                                    </button>
                                                ) : (
                                                    <button
                                                        onClick={() => setDeleteModal({ isOpen: true, newsId: item.id })}
                                                        className="p-2.5 bg-white border border-slate-100 text-rose-400 hover:text-rose-600 hover:bg-rose-50 hover:border-rose-200 rounded-xl transition-all shadow-sm"
                                                    >
                                                        <Trash2 size={16} />
                                                    </button>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>

                {/* Modern Pagination */}
                <div className="px-8 py-6 bg-slate-50/50 border-t border-slate-100 flex items-center justify-between">
                    <p className="text-xs font-bold text-slate-400 uppercase tracking-wider">
                        Displaying <span className="text-slate-900">{news.length}</span> results
                    </p>
                    <div className="flex items-center gap-2">
                        <button className="p-2 rounded-xl border border-slate-200 text-slate-400 hover:bg-white transition-colors disabled:opacity-30" disabled>
                            <ChevronLeft size={18} />
                        </button>
                        <button className="w-10 h-10 rounded-xl bg-primary-600 text-white font-black text-xs shadow-lg shadow-primary-500/20">1</button>
                        <button className="p-2 rounded-xl border border-slate-200 text-slate-400 hover:bg-white transition-colors disabled:opacity-30" disabled>
                            <ChevronRight size={18} />
                        </button>
                    </div>
                </div>
            </div>

            <ConfirmModal
                isOpen={deleteModal.isOpen}
                onClose={() => setDeleteModal({ isOpen: false, newsId: null })}
                onConfirm={handleDelete}
                title="Archive Article"
                message="Are you sure you want to move this article to the trash? You can restore it later."
                confirmText="Move to Trash"
                variant="danger"
            />
        </div>
    );
};
