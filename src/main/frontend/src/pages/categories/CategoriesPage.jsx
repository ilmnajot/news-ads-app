import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, ToggleLeft, ToggleRight, FolderTree, ArrowUpRight, Search, Activity, Save, Languages, Globe } from 'lucide-react';
import toast from 'react-hot-toast';
import { categoriesAPI } from '../../api/categories';
import { Button } from '../../components/common/Button';
import { Card } from '../../components/common/Card';
import { PageLoader } from '../../components/common/Loading';
import { Modal, ConfirmModal } from '../../components/common/Modal';
import { Input, Select, TextArea } from '../../components/common/FormFields';

export const CategoriesPage = () => {
    const [categories, setCategories] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');

    // Modal states
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [selectedCategory, setSelectedCategory] = useState(null);
    const [isSaving, setIsSaving] = useState(false);
    const [activeTranslationTab, setActiveTranslationTab] = useState('uz');

    const [formData, setFormData] = useState({
        parentId: 0,
        translations: {
            uz: { title: '', description: '', lang: 'uz' },
            ru: { title: '', description: '', lang: 'ru' },
            en: { title: '', description: '', lang: 'en' },
        }
    });

    useEffect(() => {
        fetchCategories();
    }, []);

    const fetchCategories = async () => {
        try {
            setLoading(true);
            const response = await categoriesAPI.getAll();
            setCategories(response || []);
        } catch (error) {
            console.error('Error fetching categories:', error);
            toast.error('Failed to load categories');
        } finally {
            setLoading(false);
        }
    };

    const handleOpenModal = (category = null) => {
        if (category) {
            setSelectedCategory(category);

            // Map translations from the category object
            const trans = {
                uz: { title: '', description: '', lang: 'uz' },
                ru: { title: '', description: '', lang: 'ru' },
                en: { title: '', description: '', lang: 'en' },
            };

            category.translationsDtoList?.forEach(t => {
                if (trans[t.lang]) {
                    trans[t.lang] = { title: t.title, description: t.description || '', lang: t.lang };
                }
            });

            setFormData({
                parentId: category.parentId || 0,
                translations: trans
            });
        } else {
            setSelectedCategory(null);
            setFormData({
                parentId: 0,
                translations: {
                    uz: { title: '', description: '', lang: 'uz' },
                    ru: { title: '', description: '', lang: 'ru' },
                    en: { title: '', description: '', lang: 'en' },
                }
            });
        }
        setIsModalOpen(true);
    };

    const handleTranslationChange = (lang, field, value) => {
        setFormData({
            ...formData,
            translations: {
                ...formData.translations,
                [lang]: {
                    ...formData.translations[lang],
                    [field]: value
                }
            }
        });
    };

    const handleSaveCategory = async (e) => {
        e.preventDefault();
        setIsSaving(true);

        try {
            // Transform internal form state to backend DTO structure
            const payload = {
                parentId: parseInt(formData.parentId) || 0,
                translationsDtoList: Object.values(formData.translations).filter(t => t.title.trim() !== '')
            };

            if (selectedCategory) {
                await categoriesAPI.update(selectedCategory.id, payload);
                toast.success('Category updated');
            } else {
                await categoriesAPI.create(payload);
                toast.success('Category added');
            }

            setIsModalOpen(false);
            fetchCategories();
        } catch (error) {
            console.error('Error saving category:', error);
            toast.error(error.response?.data?.message || 'Failed to save category');
        } finally {
            setIsSaving(false);
        }
    };

    const handleToggleStatus = async (id) => {
        try {
            await categoriesAPI.toggleStatus(id);
            toast.success('Status updated');
            fetchCategories();
        } catch (error) {
            console.error('Error toggling status:', error);
            toast.error('Failed to update status');
        }
    };

    const handleDelete = async () => {
        try {
            await categoriesAPI.delete(selectedCategory.id);
            toast.success('Category deleted');
            setIsDeleting(false);
            fetchCategories();
        } catch (error) {
            console.error('Error deleting category:', error);
            toast.error('Failed to delete category');
        }
    };

    const filteredCategories = categories.filter(cat =>
        cat.translationsDtoList?.some(t => t.title?.toLowerCase().includes(searchTerm.toLowerCase()))
    );

    const languages = [
        { code: 'uz', label: 'Uzbekistan', flag: '🇺🇿' },
        { code: 'ru', label: 'Russian', flag: '🇷🇺' },
        { code: 'en', label: 'English', flag: '🇺🇸' },
    ];

    if (loading && categories.length === 0) return <PageLoader />;

    return (
        <div className="space-y-12 animate-fade-in">
            {/* Ultra-Modern Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                    <div className="flex items-center gap-2 mb-2">
                        <div className="p-1.5 bg-indigo-50 text-indigo-600 rounded-lg">
                            <FolderTree size={14} />
                        </div>
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Content</span>
                    </div>
                    <h1 className="text-4xl font-black text-slate-900 tracking-tight font-heading">
                        Manage <span className="text-primary-600">Categories</span>
                    </h1>
                </div>

                <Button
                    variant="primary"
                    size="lg"
                    className="shadow-2xl shadow-primary-500/30 font-bold"
                    onClick={() => handleOpenModal()}
                >
                    <Plus size={20} className="mr-2" />
                    Add Category
                </Button>
            </div>

            {/* Smart Search Bar */}
            <div className="max-w-md">
                <div className="relative group">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                        <Search size={18} className="text-slate-400 group-focus-within:text-primary-500 transition-colors" />
                    </div>
                    <input
                        type="text"
                        placeholder="Search categories..."
                        className="w-full pl-11 pr-4 py-3.5 bg-white border border-slate-100 rounded-2xl shadow-sm focus:outline-none focus:ring-4 focus:ring-primary-100 transition-all font-medium text-slate-700"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
            </div>

            {/* Premium Structure Table */}
            <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-[0_20px_60px_-15px_rgba(0,0,0,0.03)] overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full border-collapse">
                        <thead>
                            <tr className="bg-slate-50/50 border-b border-slate-100">
                                <th className="px-8 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Category Name</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Parent</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Status</th>
                                <th className="px-8 py-5 text-right text-[10px] font-bold uppercase tracking-wider text-slate-400">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-50">
                            {filteredCategories.length === 0 ? (
                                <tr>
                                    <td colSpan="4" className="p-32 text-center">
                                        <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-300">
                                            <FolderTree size={32} />
                                        </div>
                                        <p className="text-slate-400 font-bold tracking-tight">No categories found</p>
                                    </td>
                                </tr>
                            ) : (
                                filteredCategories.map((category) => (
                                    <tr key={category.id} className="group hover:bg-slate-50/50 transition-colors">
                                        <td className="px-8 py-6">
                                            <div className="flex items-center gap-4">
                                                <div className={`w-12 h-12 rounded-2xl bg-white border border-slate-100 flex items-center justify-center group-hover:scale-110 group-hover:border-primary-100 transition-all duration-300 shadow-sm`}>
                                                    <FolderTree size={20} className={category.isActive ? 'text-primary-600' : 'text-slate-300'} />
                                                </div>
                                                <div>
                                                    <div className="text-sm font-bold text-slate-900 mb-1 group-hover:text-primary-600 transition-colors">
                                                        {category.translationsDtoList?.[0]?.title || 'Untitled'}
                                                    </div>
                                                    <p className="text-[10px] font-medium text-slate-400 flex items-center gap-1">
                                                        ID: {category.id} • /{category.translationsDtoList?.[0]?.slug || category.translationsDtoList?.[0]?.title?.toLowerCase().replace(/\s+/g, '-')}
                                                    </p>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-6">
                                            <div className="flex items-center gap-2">
                                                <div className="w-2 h-2 rounded-full bg-slate-200"></div>
                                                <span className="text-xs font-medium text-slate-600">
                                                    {category.parentId ? `Child of #${category.parentId}` : 'Main Level'}
                                                </span>
                                            </div>
                                        </td>
                                        <td className="px-6 py-6">
                                            <button
                                                onClick={() => handleToggleStatus(category.id)}
                                                className={`flex items-center gap-2 px-3 py-1.5 rounded-full text-[10px] font-black uppercase tracking-wider transition-all border ${category.isActive
                                                    ? 'bg-emerald-50 text-emerald-600 border-emerald-100'
                                                    : 'bg-slate-100 text-slate-400 border-slate-200'
                                                    }`}
                                            >
                                                {category.isActive ? <ToggleRight size={14} /> : <ToggleLeft size={14} />}
                                                {category.isActive ? 'Active' : 'Inactive'}
                                            </button>
                                        </td>
                                        <td className="px-8 py-6">
                                            <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                                                <button
                                                    onClick={() => handleOpenModal(category)}
                                                    className="p-2.5 bg-white border border-slate-100 text-slate-400 hover:text-primary-600 hover:border-primary-100 rounded-xl transition-all shadow-sm"
                                                >
                                                    <Edit size={16} />
                                                </button>
                                                <button
                                                    onClick={() => { setSelectedCategory(category); setIsDeleting(true); }}
                                                    className="p-2.5 bg-white border border-slate-100 text-rose-400 hover:text-rose-600 hover:bg-rose-50 hover:border-rose-200 rounded-xl transition-all shadow-sm"
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

            {/* Layout Footer Details */}

            {/* Category Modal */}
            <Modal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                title={selectedCategory ? "Edit Category" : "Add Category"}
                size="lg"
            >
                <form onSubmit={handleSaveCategory} className="space-y-8">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="space-y-2">
                            <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider ml-1">Parent Category</label>
                            <Select
                                value={formData.parentId}
                                onChange={(e) => setFormData({ ...formData, parentId: e.target.value })}
                                options={[
                                    { value: 0, label: 'None (Main level)' },
                                    ...categories.filter(c => c.id !== selectedCategory?.id).map(c => ({
                                        value: c.id,
                                        label: c.translationsDtoList?.[0]?.title || `Category #${c.id}`
                                    }))
                                ]}
                                className="font-bold text-sm"
                            />
                        </div>
                    </div>

                    <div className="space-y-6">
                        <div className="flex bg-slate-50 p-1.5 rounded-2xl border border-slate-100">
                            {languages.map((lang) => (
                                <button
                                    key={lang.code}
                                    type="button"
                                    onClick={() => setActiveTranslationTab(lang.code)}
                                    className={`flex-1 px-4 py-2.5 rounded-xl text-[10px] font-bold uppercase tracking-wider transition-all flex items-center justify-center gap-2 ${activeTranslationTab === lang.code
                                        ? 'bg-white text-primary-600 shadow-sm'
                                        : 'text-slate-400 hover:text-slate-600'
                                        }`}
                                >
                                    <span>{lang.flag}</span>
                                    {lang.label}
                                </button>
                            ))}
                        </div>

                        <div className="bg-slate-50/50 p-6 rounded-[2rem] border border-slate-100 space-y-6">
                            <Input
                                label="Title"
                                placeholder="Enter category name..."
                                value={formData.translations[activeTranslationTab].title}
                                onChange={(e) => handleTranslationChange(activeTranslationTab, 'title', e.target.value)}
                                required={activeTranslationTab === 'uz'}
                                className="font-bold text-xl bg-white"
                            />

                            <TextArea
                                label="Description"
                                placeholder="Short description..."
                                value={formData.translations[activeTranslationTab].description}
                                onChange={(e) => handleTranslationChange(activeTranslationTab, 'description', e.target.value)}
                                rows={3}
                                className="bg-white"
                            />
                        </div>
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
                                    <Save size={18} /> {selectedCategory ? "Save Changes" : "Save Category"}
                                </span>
                            )}
                        </Button>
                    </div>
                </form>
            </Modal>

            <ConfirmModal
                isOpen={isDeleting}
                onClose={() => setIsDeleting(false)}
                onConfirm={handleDelete}
                title="Delete Category"
                message="Are you sure you want to delete this category? This will affect sub-categories as well."
                confirmText="Delete Category"
                variant="danger"
            />
        </div>
    );
};
