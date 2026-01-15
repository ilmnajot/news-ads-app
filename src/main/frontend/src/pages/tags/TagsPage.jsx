import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Tag, Hash, Search, TrendingUp, Sparkles, Filter, X, Save } from 'lucide-react';
import toast from 'react-hot-toast';
import { tagsAPI } from '../../api/tags';
import { Button } from '../../components/common/Button';
import { Card } from '../../components/common/Card';
import { PageLoader } from '../../components/common/Loading';
import { Modal, ConfirmModal } from '../../components/common/Modal';
import { Input } from '../../components/common/FormFields';

export const TagsPage = () => {
    const [tags, setTags] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');

    // Modal states
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [selectedTag, setSelectedTag] = useState(null);
    const [tagFormData, setTagFormData] = useState({ code: '' });
    const [isSaving, setIsSaving] = useState(false);

    useEffect(() => {
        fetchTags();
    }, []);

    const fetchTags = async () => {
        try {
            setLoading(true);
            const response = await tagsAPI.getAll();
            setTags(response || []);
        } catch (error) {
            console.error('Error fetching tags:', error);
            toast.error('Failed to load tags');
        } finally {
            setLoading(false);
        }
    };

    const handleOpenModal = (tag = null) => {
        if (tag) {
            setSelectedTag(tag);
            setTagFormData({ code: tag.code });
        } else {
            setSelectedTag(null);
            setTagFormData({ code: '' });
        }
        setIsModalOpen(true);
    };

    const handleSaveTag = async (e) => {
        e.preventDefault();
        setIsSaving(true);
        try {
            if (selectedTag) {
                await tagsAPI.update(selectedTag.id, tagFormData);
                toast.success('Tag updated');
            } else {
                await tagsAPI.create(tagFormData);
                toast.success('Tag added');
            }
            setIsModalOpen(false);
            fetchTags();
        } catch (error) {
            console.error('Error saving tag:', error);
            toast.error(error.response?.data?.message || 'Failed to save tag');
        } finally {
            setIsSaving(false);
        }
    };

    const handleDeleteTag = async () => {
        try {
            await tagsAPI.delete(selectedTag.id);
            toast.success('Tag deleted');
            setIsDeleting(false);
            fetchTags();
        } catch (error) {
            console.error('Error deleting tag:', error);
            toast.error('Failed to delete tag');
        }
    };

    const filteredTags = tags.filter(tag =>
        tag.code?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        tag.translationsDtoList?.[0]?.name?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading && tags.length === 0) return <PageLoader />;

    return (
        <div className="space-y-12 animate-fade-in pb-20">
            {/* Modern Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                    <div className="flex items-center gap-2 mb-2">
                        <div className="p-1.5 bg-primary-100 text-primary-600 rounded-lg">
                            <Tag size={14} />
                        </div>
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Content</span>
                    </div>
                    <h1 className="text-4xl font-black text-slate-900 tracking-tight font-heading">
                        Manage <span className="text-primary-600">Tags</span>
                    </h1>
                </div>

                <Button
                    variant="primary"
                    size="lg"
                    className="shadow-2xl shadow-primary-500/30 font-bold"
                    onClick={() => handleOpenModal()}
                >
                    <Plus size={20} className="mr-2" />
                    Add Tag
                </Button>
            </div>

            {/* Smart Search & Filter */}
            <div className="bg-white rounded-[2rem] p-4 border border-slate-100 shadow-xl shadow-slate-200/40 flex flex-col sm:flex-row items-center gap-4 max-w-2xl">
                <div className="relative flex-1 w-full group">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                        <Search size={18} className="text-slate-400 group-focus-within:text-primary-500 transition-colors" />
                    </div>
                    <input
                        type="text"
                        placeholder="Search tags..."
                        className="w-full pl-11 pr-4 py-3 bg-slate-50 border-transparent rounded-[1.2rem] focus:outline-none focus:ring-4 focus:ring-primary-100 focus:bg-white transition-all text-sm font-medium"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
            </div>

            {/* Modern Tag Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                {filteredTags.length === 0 ? (
                    <div className="col-span-full py-32 text-center bg-white border border-slate-100 rounded-[3rem] shadow-xl shadow-slate-200/40">
                        <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-200">
                            <Hash size={32} />
                        </div>
                        <p className="text-slate-400 font-bold tracking-tight italic">No associated hashtags found</p>
                    </div>
                ) : (
                    filteredTags.map((tag, i) => (
                        <div
                            key={tag.id}
                            className="group relative bg-white rounded-[2.2rem] p-6 border border-slate-100 shadow-[0_10px_40px_-15px_rgba(0,0,0,0.03)] hover:shadow-[0_20px_60px_-10px_rgba(0,0,0,0.07)] hover:-translate-y-1 transition-all duration-500 animate-fade-in"
                            style={{ animationDelay: `${i * 0.05}s` }}
                        >
                            <div className="flex items-start justify-between mb-6">
                                <div className="w-12 h-12 rounded-2xl bg-primary-50 flex items-center justify-center text-primary-600 group-hover:bg-primary-600 group-hover:text-white transition-all duration-500 shadow-sm border border-primary-100/50">
                                    <Hash size={20} strokeWidth={2.5} />
                                </div>
                                <div className="flex gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                                    <button
                                        onClick={() => handleOpenModal(tag)}
                                        className="p-2 bg-slate-50 hover:bg-white text-slate-400 hover:text-indigo-600 rounded-xl transition-all shadow-none hover:shadow-sm border border-transparent hover:border-indigo-100"
                                    >
                                        <Edit size={14} />
                                    </button>
                                    <button
                                        onClick={() => { setSelectedTag(tag); setIsDeleting(true); }}
                                        className="p-2 bg-slate-50 hover:bg-white text-slate-400 hover:text-rose-600 rounded-xl transition-all shadow-none hover:shadow-sm border border-transparent hover:border-rose-100"
                                    >
                                        <Trash2 size={14} />
                                    </button>
                                </div>
                            </div>

                            <h3 className="text-lg font-bold text-slate-900 tracking-tight mb-1 group-hover:text-primary-600 transition-colors">#{tag.code}</h3>
                            <p className="text-[10px] font-bold text-slate-400 uppercase tracking-wider leading-none">
                                {tag.translationsDtoList?.[0]?.name || 'Internal'}
                            </p>

                            <div className="mt-6 pt-6 border-t border-slate-50 flex items-center justify-between">
                                <span className={`text-[9px] font-bold uppercase tracking-wider flex items-center gap-1 ${tag.isActive ? 'text-emerald-500' : 'text-slate-300'}`}>
                                    <TrendingUp size={10} /> {tag.isActive ? 'Active' : 'Hidden'}
                                </span>
                                <span className="text-[9px] font-bold text-slate-300">ID: {tag.id}</span>
                            </div>
                        </div>
                    ))
                )}
            </div>

            {/* Tag Creation/Edit Modal */}
            <Modal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                title={selectedTag ? "Edit Tag" : "Add Tag"}
            >
                <form onSubmit={handleSaveTag} className="space-y-6">

                    <div className="space-y-4">
                        <Input
                            label="Tag Code"
                            placeholder="e.g. tech-insights"
                            value={tagFormData.code}
                            onChange={(e) => setTagFormData({ ...tagFormData, code: e.target.value })}
                            required
                            className="text-lg font-bold"
                        />
                        <p className="text-[10px] text-slate-400 font-medium leading-relaxed">
                            Use lowercase letters, numbers, and hyphens only.
                        </p>
                    </div>

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
                            disabled={isSaving}
                            className="flex-1 shadow-lg shadow-primary-500/20 font-bold"
                        >
                            {isSaving ? "Saving..." : (
                                <span className="flex items-center gap-2">
                                    <Save size={18} /> {selectedTag ? "Save Changes" : "Save Tag"}
                                </span>
                            )}
                        </Button>
                    </div>
                </form>
            </Modal>

            <ConfirmModal
                isOpen={isDeleting}
                onClose={() => setIsDeleting(false)}
                onConfirm={handleDeleteTag}
                title="Delete Tag"
                message="Are you sure you want to delete this tag? This action cannot be undone."
                confirmText="Delete Tag"
                variant="danger"
            />
        </div>
    );
};
