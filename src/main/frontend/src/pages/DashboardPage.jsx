import { Newspaper, FolderTree, Tag, Megaphone, TrendingUp, Users, ArrowUpRight, Zap } from 'lucide-react';

const StatCard = ({ icon: Icon, label, value, color, delay }) => (
    <div
        className="group relative overflow-hidden bg-white rounded-[2.5rem] p-8 border border-slate-100 shadow-[0_10px_40px_-15px_rgba(0,0,0,0.03)] hover:shadow-[0_20px_50px_-10px_rgba(0,0,0,0.05)] transition-all duration-500 animate-fade-in"
        style={{ animationDelay: delay }}
    >
        {/* Glow Effect */}
        <div className={`absolute -right-8 -top-8 w-32 h-32 bg-${color}-500/5 rounded-full blur-3xl group-hover:scale-150 transition-transform duration-700`}></div>

        <div className="relative z-10">
            <div className="flex items-center justify-between mb-6">
                <div className={`w-14 h-14 rounded-3xl bg-${color}-50 flex items-center justify-center text-${color}-600 group-hover:bg-${color}-500 group-hover:text-white transition-all duration-500 shadow-sm`}>
                    <Icon size={26} strokeWidth={2.5} />
                </div>
                <div className="flex items-center gap-1.5 px-3 py-1.5 bg-emerald-50 rounded-full text-emerald-600 text-[10px] font-black uppercase tracking-wider">
                    <ArrowUpRight size={12} />
                    12%
                </div>
            </div>

            <h3 className="text-slate-400 text-xs font-bold uppercase tracking-wider mb-2">{label}</h3>
            <p className="text-4xl font-bold text-slate-900 tracking-tight">{value}</p>
        </div>
    </div>
);

export const DashboardPage = () => {
    return (
        <div className="space-y-12">
            {/* Welcome Section */}
            <div className="flex flex-col md:flex-row md:items-end justify-between gap-6">
                <div className="animate-fade-in">
                    <div className="flex items-center gap-2 mb-3">
                        <span className="px-3 py-1 bg-primary-100 text-primary-600 rounded-full text-[10px] font-bold uppercase tracking-wider">Dashboard</span>
                        <div className="flex h-2 w-2 relative">
                            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                            <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
                        </div>
                    </div>
                    <h1 className="text-5xl font-black text-slate-900 tracking-tight font-heading mb-3">
                        Welcome, <span className="text-primary-600">Admin</span>
                    </h1>
                    <p className="text-slate-500 text-lg font-medium">Here is what's happening on your platform today.</p>
                </div>

                <div className="flex gap-3 animate-fade-in" style={{ animationDelay: '0.1s' }}>
                    <button className="flex items-center gap-2 px-6 py-3.5 bg-white border border-slate-100 shadow-sm rounded-2xl text-slate-700 font-bold text-sm hover:border-slate-200 transition-all">
                        Generate Report
                    </button>
                    <button className="flex items-center gap-2 px-6 py-3.5 bg-primary-600 text-white shadow-xl shadow-primary-500/20 rounded-2xl font-bold text-sm hover:bg-primary-700 transition-all">
                        <Zap size={18} fill="currentColor" />
                        Quick Create
                    </button>
                </div>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
                <StatCard icon={Newspaper} label="Total Articles" value="1,280" color="blue" delay="0.2s" />
                <StatCard icon={Users} label="Total Users" value="84.2k" color="indigo" delay="0.3s" />
                <StatCard icon={TrendingUp} label="Total Views" value="482k" color="emerald" delay="0.4s" />
                <StatCard icon={Megaphone} label="Active Ads" value="42" color="amber" delay="0.5s" />
            </div>

            {/* Main Content Grid */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                {/* Visual Placeholder for Analytics */}
                <div className="lg:col-span-2 group relative bg-slate-950 rounded-[3rem] p-10 overflow-hidden shadow-2xl animate-fade-in transition-transform duration-700 hover:scale-[1.01]" style={{ animationDelay: '0.6s' }}>
                    <div className="absolute inset-0 bg-gradient-to-br from-primary-600/20 to-indigo-600/20 opacity-50"></div>
                    <div className="absolute inset-0 bg-[url('https://grainy-gradients.vercel.app/noise.svg')] opacity-20 contrast-150"></div>

                    <div className="relative z-10">
                        <div className="flex items-center justify-between mb-12">
                            <div>
                                <h3 className="text-white text-2xl font-bold font-heading mb-1">Growth</h3>
                                <p className="text-slate-400 text-sm font-medium">Performance over time</p>
                            </div>
                            <div className="flex gap-2">
                                {['Day', 'Week', 'Month'].map(t => (
                                    <button key={t} className={`px-4 py-2 rounded-xl text-[10px] font-bold uppercase tracking-wider transition-colors ${t === 'Week' ? 'bg-primary-600 text-white' : 'text-slate-400 hover:text-white hover:bg-white/5'}`}>{t}</button>
                                ))}
                            </div>
                        </div>

                        {/* Abstract Chart Representation */}
                        <div className="h-64 flex items-end gap-3 px-4">
                            {[40, 70, 45, 90, 65, 80, 50, 95, 75, 85, 60, 100].map((h, i) => (
                                <div
                                    key={i}
                                    className="flex-1 bg-gradient-to-t from-primary-500/0 via-primary-500/50 to-primary-500 rounded-t-lg transition-all duration-1000 hover:brightness-125 cursor-help"
                                    style={{ height: `${h}%`, transitionDelay: `${i * 50}ms` }}
                                ></div>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Quick Actions Panel */}
                <div className="space-y-8 animate-fade-in" style={{ animationDelay: '0.7s' }}>
                    <div className="bg-white rounded-[3rem] p-8 border border-slate-100 shadow-xl shadow-slate-200/40">
                        <h3 className="text-xl font-bold text-slate-900 font-heading mb-6 tracking-tight">Quick Actions</h3>
                        <div className="space-y-4">
                            {[
                                { icon: Newspaper, label: 'Add Article', color: 'blue', path: '/admin/news/create' },
                                { icon: FolderTree, label: 'Manage Categories', color: 'indigo', path: '/admin/categories' },
                                { icon: Megaphone, label: 'Create Campaign', color: 'amber', path: '/admin/campaigns' },
                            ].map((action, i) => (
                                <a
                                    key={i}
                                    href={action.path}
                                    className="flex items-center gap-4 p-4 bg-slate-50 hover:bg-slate-100 rounded-2xl transition-all group"
                                >
                                    <div className={`w-10 h-10 rounded-xl bg-${action.color}-100 flex items-center justify-center text-${action.color}-600 group-hover:scale-110 transition-transform`}>
                                        <action.icon size={18} />
                                    </div>
                                    <span className="font-bold text-sm text-slate-700">{action.label}</span>
                                    <ChevronRight size={16} className="ml-auto text-slate-300 group-hover:translate-x-1 transition-transform" />
                                </a>
                            ))}
                        </div>
                    </div>

                    {/* Pro Tip Card */}
                    <div className="bg-gradient-to-tr from-primary-600 to-indigo-600 rounded-[3rem] p-8 text-white relative overflow-hidden group">
                        <Zap className="absolute right-[-10%] top-[-10%] w-32 h-32 opacity-10 group-hover:scale-110 transition-transform duration-700" size={100} fill="currentColor" />
                        <h4 className="text-lg font-black font-heading mb-2 relative z-10">Pro Analytics Tip</h4>
                        <p className="text-primary-100 text-sm font-medium relative z-10 leading-relaxed mb-4">
                            You can now schedule automated reports for your advertising campaigns directly from the assignments tab.
                        </p>
                        <button className="text-white font-bold text-[10px] uppercase tracking-wider relative z-10 flex items-center gap-2 group-hover:gap-3 transition-all">
                            Learn More <ArrowUpRight size={12} />
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

const ChevronRight = ({ size, className }) => (
    <svg
        width={size}
        height={size}
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2.5"
        strokeLinecap="round"
        strokeLinejoin="round"
        className={className}
    >
        <path d="m9 18 6-6-6-6" />
    </svg>
);
