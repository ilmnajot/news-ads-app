import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
    Save, X, Languages, Globe, Image as ImageIcon, Sparkles,
    ArrowLeft, Send, SaveAll, Loader2, Plus, Hash
} from 'lucide-react';
import toast from 'react-hot-toast';
import { newsAPI } from '../../api/news';
import { categoriesAPI } from '../../api/categories';
import { tagsAPI } from '../../api/tags';
import { mediaAPI } from '../../api/media';
import { Button } from '../../components/common/Button';
import { Card } from '../../components/common/Card';
import { PageLoader } from '../../components/common/Loading';
import { Input, TextArea, Select } from '../../components/common/FormFields';

export const NewsCreatePage = () => {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [isSaving, setIsSaving] = useState(false);

    // Data for selects
    const [categories, setCategories] = useState([]);
    const [availableTags, setAvailableTags] = useState([]);

    // UI state
    const [activeLang, setActiveLang] = useState('uz');
    const [coverPreview, setCoverPreview] = useState(null);
    const [isUploading, setIsUploading] = useState(false);

    const [formData, setFormData] = useState({
        categoryId: '',
        status: 'DRAFT',
        isFeatured: false,
        publishAt: '',
        unpublishAt: '',
        coverMediaId: null,
        tagCodes: [],
        translations: {
            uz: { title: '', summary: '', content: '', metaTitle: '', metaDescription: '' },
            ru: { title: '', summary: '', content: '', metaTitle: '', metaDescription: '' },
            en: { title: '', summary: '', content: '', metaTitle: '', metaDescription: '' },
        }
    });

    useEffect(() => {
        initData();
    }, []);

    const initData = async () => {
        try {
            setLoading(true);
            const [catRes, tagRes] = await Promise.all([
                categoriesAPI.getAll(),
                tagsAPI.getAll({ page: 0, size: 100 })
            ]);
            setCategories(catRes.data || []);
            setAvailableTags(tagRes.data || []);
        } catch (error) {
            console.error('Initialization error:', error);
            toast.error('Failed to load article dependencies');
        } finally {
            setLoading(false);
        }
    };

    const handleFileUpload = async (e) => {
        const file = e.target.files[0];
        if (!file) return;

        setIsUploading(true);
        try {
            const response = await mediaAPI.upload(file);
            // Assuming response.data contains the uploaded media object with id and url
            setFormData({ ...formData, coverMediaId: response.data.id });
            setCoverPreview(URL.createObjectURL(file));
            toast.success('Media asset synchronized');
        } catch (error) {
            console.error('Upload error:', error);
            toast.error('Failed to upload image');
        } finally {
            setIsUploading(false);
        }
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

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validate required fields
        if (!formData.categoryId) return toast.error('Please select a category');
        if (!formData.translations.uz.title) return toast.error('Uzbek title is mandatory');

        setIsSaving(true);
        try {
            // Transform form data to Match NewsCreateRequest DTO
            const payload = {
                categoryId: parseInt(formData.categoryId),
                status: formData.status,
                isFeatured: formData.isFeatured,
                coverMediaId: formData.coverMediaId,
                tagCodes: formData.tagCodes,
                publishAt: formData.publishAt ? `${formData.publishAt}T00:00:00` : null,
                unpublishAt: formData.unpublishAt ? `${formData.unpublishAt}T23:59:59` : null,
                translations: Object.fromEntries(
                    Object.entries(formData.translations).filter(([_, trans]) => trans.title.trim() !== '')
                )
            };

            await newsAPI.create(payload);
            toast.success('Article published');
            navigate('/admin/news');
        } catch (error) {
            console.error('Publish error:', error);
            toast.error(error.response?.data?.message || 'Failed to publish article');
        } finally {
            setIsSaving(false);
        }
    };

    const handleTagToggle = (code) => {
        const newTags = formData.tagCodes.includes(code)
            ? formData.tagCodes.filter(c => c !== code)
            : [...formData.tagCodes, code];
        setFormData({ ...formData, tagCodes: newTags });
    };

    if (loading) return <PageLoader />;

    return (
        <div className="max-w-[1400px] mx-auto pb-24 animate-fade-in">
            {/* Action Bar */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 mb-12">
                <div className="flex items-center gap-6">
                    <button
                        onClick={() => navigate('/admin/news')}
                        className="p-4 bg-white rounded-2xl border border-slate-100 text-slate-400 hover:text-primary-600 hover:border-primary-100 transition-all shadow-sm group"
                    >
                        <ArrowLeft size={24} className="group-hover:-translate-x-1 transition-transform" />
                    </button>
                    <div>
                        <div className="flex items-center gap-2 mb-2">
                            <div className="p-1 px-2 bg-primary-50 text-primary-600 rounded-md text-[10px] font-bold tracking-wider uppercase">
                                Articles
                            </div>
                        </div>
                        <h1 className="text-4xl font-black text-slate-900 tracking-tight font-heading">
                            Add New <span className="text-primary-600">Article</span>
                        </h1>
                    </div>
                </div>

                <div className="flex gap-3">
                    <Button variant="secondary" size="lg" onClick={() => navigate('/admin/news')}>
                        Cancel
                    </Button>
                    <Button
                        variant="primary"
                        size="lg"
                        onClick={handleSubmit}
                        disabled={isSaving}
                        className="shadow-2xl shadow-primary-500/30 min-w-[160px]"
                    >
                        {isSaving ? <Loader2 className="animate-spin" /> : (
                            <span className="flex items-center gap-2">
                                <Send size={18} /> Publish Article
                            </span>
                        )}
                    </Button>
                </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-12 gap-10">
                {/* Main Content Area */}
                <div className="lg:col-span-8 space-y-10">
                    {/* Multi-language Tabs */}
                    <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40 p-10">
                        <div className="flex gap-4 mb-10 overflow-x-auto pb-2">
                            {['uz', 'ru', 'en'].map(lang => (
                                <button
                                    key={lang}
                                    onClick={() => setActiveLang(lang)}
                                    className={`px-8 py-3.5 rounded-2xl text-[10px] font-black uppercase tracking-[2px] transition-all flex items-center gap-3 border ${activeLang === lang
                                        ? 'bg-slate-900 text-white border-slate-900 shadow-xl'
                                        : 'bg-slate-50 text-slate-400 border-slate-100 hover:bg-white'
                                        }`}
                                >
                                    <Globe size={14} className={activeLang === lang ? 'text-primary-400' : ''} />
                                    {lang === 'uz' ? 'Uzbek' : lang === 'ru' ? 'Russian' : 'English'}
                                </button>
                            ))}
                        </div>

                        <div className="space-y-8">
                            <textarea
                                placeholder="Article title..."
                                className="w-full text-4xl md:text-5xl font-black text-slate-900 placeholder:text-slate-100 focus:outline-none bg-transparent resize-none leading-tight border-b border-transparent focus:border-slate-50 transition-all pb-4 min-h-[120px]"
                                value={formData.translations[activeLang].title}
                                onChange={(e) => handleTranslationChange(activeLang, 'title', e.target.value)}
                            />

                            <Input
                                label="Slug"
                                placeholder="e.g. major-breakthrough"
                                value={formData.translations[activeLang].slug}
                                onChange={(e) => handleTranslationChange(activeLang, 'slug', e.target.value)}
                                className="bg-slate-50 border-none font-bold text-slate-600"
                            />

                            <TextArea
                                label="Summary"
                                placeholder="Short summary..."
                                value={formData.translations[activeLang].summary}
                                onChange={(e) => handleTranslationChange(activeLang, 'summary', e.target.value)}
                                rows={2}
                                className="bg-slate-50 border-none"
                            />

                            <div className="space-y-2">
                                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider ml-1">Article Content</label>
                                <TextArea
                                    placeholder="Write your story here..."
                                    value={formData.translations[activeLang].content}
                                    onChange={(e) => handleTranslationChange(activeLang, 'content', e.target.value)}
                                    rows={15}
                                    className="bg-slate-50 border-none leading-relaxed text-lg"
                                />
                            </div>
                        </div>
                    </div>

                    {/* SEO Insights */}
                    <div className="bg-slate-900 rounded-[2.5rem] p-10 text-white relative overflow-hidden group">
                        <Sparkles size={100} className="absolute right-[-10px] top-[-10px] text-white opacity-5 transition-transform group-hover:rotate-12 duration-1000" />
                        <h3 className="text-xl font-bold mb-6 flex items-center gap-3">
                            <Sparkles className="text-primary-400" /> SEO Settings
                        </h3>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <Input
                                label="Meta Title"
                                placeholder="SEO Title..."
                                value={formData.translations[activeLang].metaTitle}
                                onChange={(e) => handleTranslationChange(activeLang, 'metaTitle', e.target.value)}
                                className="bg-white/5 border-white/10 text-white focus:bg-white/10"
                            />
                            <Input
                                label="Meta Description"
                                placeholder="SEO Description..."
                                value={formData.translations[activeLang].metaDescription}
                                onChange={(e) => handleTranslationChange(activeLang, 'metaDescription', e.target.value)}
                                className="bg-white/5 border-white/10 text-white focus:bg-white/10"
                            />
                        </div>
                    </div>
                </div>

                {/* Sidebar Configuration */}
                <div className="lg:col-span-4 space-y-10">
                    {/* Media Card */}
                    <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40 p-10">
                        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-6 flex items-center gap-2">
                            <ImageIcon size={14} className="text-primary-600" /> Cover Image
                        </h3>

                        <div className="relative group rounded-3xl overflow-hidden aspect-video bg-slate-50 border-2 border-dashed border-slate-100 flex items-center justify-center transition-all hover:border-primary-300">
                            {coverPreview ? (
                                <img src={coverPreview} alt="Cover Preview" className="w-full h-full object-cover transition-transform group-hover:scale-110" />
                            ) : (
                                <div className="text-center">
                                    <div className="w-12 h-12 rounded-2xl bg-white border border-slate-100 flex items-center justify-center mx-auto mb-3 text-slate-300">
                                        {isUploading ? <Loader2 className="animate-spin" /> : <Plus size={24} />}
                                    </div>
                                    <p className="text-[10px] font-bold text-slate-400 uppercase">Upload Image</p>
                                </div>
                            )}
                            <input
                                type="file"
                                className="absolute inset-0 opacity-0 cursor-pointer"
                                onChange={handleFileUpload}
                                accept="image/*"
                            />
                        </div>
                    </div>

                    {/* Taxonomy Card */}
                    <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40 p-10">
                        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-6">Settings</h3>
                        <div className="space-y-6">
                            <Select
                                label="Category"
                                value={formData.categoryId}
                                onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                                options={[
                                    { value: '', label: 'Select category...' },
                                    ...categories.map(c => ({ value: c.id, label: c.translationsDtoList?.[0]?.title }))
                                ]}
                            />

                            <Select
                                label="Status"
                                value={formData.status}
                                onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                                options={[
                                    { value: 'DRAFT', label: 'Draft' },
                                    { value: 'REVIEW', label: 'In Review' },
                                    { value: 'PUBLISHED', label: 'Published' },
                                    { value: 'UNPUBLISHED', label: 'Unpublished' },
                                    { value: 'ARCHIVED', label: 'Archived' },
                                ]}
                            />

                            <div className="flex items-center justify-between p-4 bg-slate-50 rounded-2xl border border-slate-100">
                                <span className="text-xs font-bold text-slate-600">Featured Article</span>
                                <button
                                    onClick={() => setFormData({ ...formData, isFeatured: !formData.isFeatured })}
                                    className={`w-12 h-6 rounded-full transition-all relative ${formData.isFeatured ? 'bg-primary-600' : 'bg-slate-200'}`}
                                >
                                    <div className={`absolute top-1 w-4 h-4 bg-white rounded-full transition-all ${formData.isFeatured ? 'left-7' : 'left-1'}`}></div>
                                </button>
                            </div>
                        </div>
                    </div>

                    {/* Scheduling Card */}
                    <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40 p-10">
                        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-6">Publishing</h3>
                        <div className="space-y-6">
                            <Input
                                type="date"
                                label="Publish At"
                                value={formData.publishAt}
                                onChange={(e) => setFormData({ ...formData, publishAt: e.target.value })}
                            />
                            <Input
                                type="date"
                                label="Unpublish At"
                                value={formData.unpublishAt}
                                onChange={(e) => setFormData({ ...formData, unpublishAt: e.target.value })}
                            />
                        </div>
                    </div>

                    {/* Hashtag Indexing */}
                    <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-xl shadow-slate-200/40 p-10">
                        <h3 className="text-xs font-bold text-slate-400 uppercase tracking-wider mb-6 flex items-center gap-2">
                            <Hash size={14} className="text-primary-600" /> Tags
                        </h3>
                        <div className="flex flex-wrap gap-2">
                            {availableTags.map(tag => (
                                <button
                                    key={tag.id}
                                    onClick={() => handleTagToggle(tag.code)}
                                    className={`px-3 py-1.5 rounded-xl text-[9px] font-bold uppercase transition-all border ${formData.tagCodes.includes(tag.code)
                                        ? 'bg-primary-600 text-white border-primary-600'
                                        : 'bg-slate-50 text-slate-400 border-slate-100 hover:border-primary-200'
                                        }`}
                                >
                                    #{tag.code}
                                </button>
                            ))}
                            {availableTags.length === 0 && (
                                <p className="text-[10px] text-slate-400 italic">No system tags available</p>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};
