import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Search, Calendar, ArrowRight, Sparkles, TrendingUp, Globe } from 'lucide-react';
import { publicNewsAPI } from '../../api/news';
import { PageLoader } from '../../components/common/Loading';

export const PublicNewsPage = () => {
    const [news, setNews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [filters, setFilters] = useState({
        keyword: '',
        lang: 'uz',
        page: 0,
        size: 12,
    });

    useEffect(() => {
        fetchNews();
    }, [filters]);

    const fetchNews = async () => {
        try {
            setLoading(true);
            const response = await publicNewsAPI.getAll(filters);
            setNews(response.data || []);
        } catch (error) {
            console.error('Error fetching news:', error);
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <PageLoader />;

    return (
        <div className="min-h-screen bg-slate-50 font-sans selection:bg-primary-100 selection:text-primary-900">
            {/* Premium Navigation */}
            <nav className="sticky top-0 z-50 glass border-b border-white/20 px-6 h-20 flex items-center justify-between">
                <Link to="/" className="flex items-center gap-2 group">
                    <div className="w-10 h-10 bg-primary-600 rounded-2xl flex items-center justify-center text-white shadow-xl shadow-primary-500/20 group-hover:rotate-12 transition-transform">
                        <TrendingUp size={20} />
                    </div>
                    <span className="text-2xl font-black text-slate-900 tracking-tighter font-heading">
                        Pulse<span className="text-primary-600">News</span>
                    </span>
                </Link>

                <div className="flex items-center gap-4">
                    <div className="hidden md:flex items-center bg-white/50 border border-slate-200 rounded-2xl px-3 py-1.5 focus-within:ring-4 focus-within:ring-primary-100 transition-all">
                        <Search size={18} className="text-slate-400" />
                        <input
                            type="text"
                            placeholder="Search stories..."
                            className="bg-transparent border-none focus:ring-0 text-sm font-medium text-slate-700 w-48 placeholder:text-slate-400"
                            value={filters.keyword}
                            onChange={(e) => setFilters({ ...filters, keyword: e.target.value })}
                        />
                    </div>

                    <select
                        className="hidden sm:block bg-white/50 border border-slate-200 rounded-2xl px-3 py-1.5 text-xs font-bold text-slate-600 focus:ring-4 focus:ring-primary-100 transition-all"
                        value={filters.lang}
                        onChange={(e) => setFilters({ ...filters, lang: e.target.value })}
                    >
                        <option value="uz">UZB</option>
                        <option value="ru">RUS</option>
                        <option value="en">ENG</option>
                    </select>

                    <Link
                        to="/login"
                        className="flex items-center gap-2 px-5 py-2.5 bg-slate-900 text-white rounded-2xl text-xs font-black uppercase tracking-wider hover:bg-slate-800 transition-all shadow-lg shadow-slate-900/10 active:scale-95"
                    >
                        Admin Log In
                    </Link>
                </div>
            </nav>

            <main className="max-w-7xl mx-auto px-6 py-12 lg:py-20 animate-fade-in">
                {/* Hero Section */}
                <div className="relative mb-20">
                    <div className="absolute -top-20 -left-20 w-64 h-64 bg-primary-500/10 rounded-full blur-3xl animate-pulse"></div>
                    <div className="relative z-10 text-center max-w-2xl mx-auto mb-16">
                        <div className="inline-flex items-center gap-2 px-4 py-2 bg-primary-100 text-primary-700 rounded-2xl text-[10px] font-black uppercase tracking-[2px] mb-6">
                            <Sparkles size={14} className="animate-spin-slow" />
                            Global News Network
                        </div>
                        <h1 className="text-6xl font-black text-slate-900 tracking-tight font-heading mb-6 leading-[1.1]">
                            Stay Ahead of the <span className="text-primary-600 italic">Curve.</span>
                        </h1>
                        <p className="text-xl text-slate-500 font-medium leading-relaxed">
                            Personalized, real-time news delivery powered by modern technology.
                        </p>
                    </div>

                    {/* Language Selector for Mobile */}
                    <div className="sm:hidden flex justify-center gap-2 mb-10">
                        {['uz', 'ru', 'en'].map(l => (
                            <button
                                key={l}
                                onClick={() => setFilters({ ...filters, lang: l })}
                                className={`px-4 py-2 rounded-xl text-[10px] font-black uppercase tracking-wider transition-all border ${filters.lang === l ? 'bg-primary-600 text-white border-primary-600 shadow-lg shadow-primary-500/20' : 'bg-white text-slate-500 border-slate-200'}`}
                            >
                                {l}
                            </button>
                        ))}
                    </div>
                </div>

                {/* Stories Grid */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-10">
                    {news.length === 0 ? (
                        <div className="col-span-full py-32 text-center bg-white border border-slate-100 rounded-[3rem] shadow-xl shadow-slate-200/40">
                            <div className="w-20 h-20 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-6 text-slate-200">
                                <Globe size={40} />
                            </div>
                            <h3 className="text-2xl font-black text-slate-900 font-heading mb-2">Silence is golden</h3>
                            <p className="text-slate-400 font-medium italic">We're currently gathering stories for you. Please check back later.</p>
                        </div>
                    ) : (
                        news.map((item, i) => (
                            <div
                                key={item.id}
                                className="group flex flex-col bg-white rounded-[2.5rem] overflow-hidden border border-slate-100 shadow-[0_10px_40px_-20px_rgba(0,0,0,0.05)] hover:shadow-[0_20px_60px_-15px_rgba(0,0,0,0.1)] transition-all duration-500 hover:-translate-y-2 animate-fade-in"
                                style={{ animationDelay: `${i * 0.1}s` }}
                            >
                                {/* Visual Placeholder */}
                                <div className="h-64 bg-slate-100 relative overflow-hidden">
                                    <div className="absolute inset-0 bg-gradient-to-br from-primary-600/20 to-indigo-600/20 opacity-0 group-hover:opacity-100 transition-opacity duration-500"></div>
                                    <div className="absolute inset-0 flex items-center justify-center text-slate-300 font-black text-6xl italic group-hover:scale-110 transition-transform duration-700 select-none">
                                        PULSE
                                    </div>
                                </div>

                                <div className="p-8 flex flex-col flex-1">
                                    <div className="flex items-center gap-3 mb-6">
                                        <span className="px-3 py-1 bg-primary-50 text-primary-600 rounded-full text-[10px] font-black uppercase tracking-wider">
                                            {item.category?.name || 'Journal'}
                                        </span>
                                        <div className="flex items-center text-[10px] font-bold text-slate-400 uppercase tracking-wider">
                                            <Calendar size={12} className="mr-1.5" />
                                            {new Date(item.publishAt).toLocaleDateString(undefined, { month: 'short', day: 'numeric', year: 'numeric' })}
                                        </div>
                                    </div>

                                    <h2 className="text-2xl font-black text-slate-900 font-heading truncate-2 mb-4 leading-tight group-hover:text-primary-600 transition-colors">
                                        {item.title}
                                    </h2>
                                    <p className="text-slate-500 font-medium text-sm line-clamp-3 mb-8 flex-1">
                                        {item.summary}
                                    </p>

                                    <Link
                                        to={`/news/${item.slug}`}
                                        className="flex items-center justify-center gap-2 w-full py-4 bg-slate-50 border border-slate-100 rounded-2xl text-slate-900 font-black text-xs uppercase tracking-wider hover:bg-slate-950 hover:text-white hover:border-slate-950 transition-all group/btn"
                                    >
                                        Read Full Story
                                        <ArrowRight size={16} className="group-hover/btn:translate-x-1 transition-transform" />
                                    </Link>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </main>

            {/* Modern Footer */}
            <footer className="mt-20 border-t border-slate-100 bg-white py-20">
                <div className="max-w-7xl mx-auto px-6 flex flex-col md:flex-row items-center justify-between gap-10">
                    <div className="text-center md:text-left">
                        <div className="text-2xl font-black text-slate-900 tracking-tighter font-heading mb-4">
                            Pulse<span className="text-primary-600">News</span>
                        </div>
                        <p className="text-slate-400 text-sm font-medium">© 2026 Global News Network. All rights reserved.</p>
                    </div>

                    <div className="flex gap-8">
                        {['About', 'Privacy', 'Contact', 'Twitter'].map(link => (
                            <a key={link} href="#" className="text-sm font-bold text-slate-500 hover:text-primary-600 transition-colors">{link}</a>
                        ))}
                    </div>
                </div>
            </footer>
        </div>
    );
};
