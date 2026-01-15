import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, LayoutGrid, Target, Layers, ArrowUpRight, Search, Activity, Settings, Save, X, Globe, FolderTree } from 'lucide-react';
import toast from 'react-hot-toast';
import { assignmentsAPI } from '../../api/assignments';
import { campaignsAPI } from '../../api/campaigns';
import { placementsAPI } from '../../api/placements';
import { creativesAPI } from '../../api/creatives';
import { categoriesAPI } from '../../api/categories';
import { Button } from '../../components/common/Button';
import { Card } from '../../components/common/Card';
import { PageLoader } from '../../components/common/Loading';
import { Modal, ConfirmModal } from '../../components/common/Modal';
import { Input, Select } from '../../components/common/FormFields';

export const AssignmentsPage = () => {
    const [assignments, setAssignments] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');

    // Dependency data
    const [campaigns, setCampaigns] = useState([]);
    const [placements, setPlacements] = useState([]);
    const [creatives, setCreatives] = useState([]);
    const [categories, setCategories] = useState([]);

    // Modal states
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [selectedAssignment, setSelectedAssignment] = useState(null);
    const [isSaving, setIsSaving] = useState(false);

    const initialFormState = {
        placementId: '',
        campaignId: '',
        creativeId: '',
        weight: 50,
        startAt: '',
        endAt: '',
        langFilter: [],
        categoryFilter: [],
        isActive: true
    };

    const [formData, setFormData] = useState(initialFormState);

    useEffect(() => {
        initData();
    }, []);

    const initData = async () => {
        try {
            setLoading(true);
            const [assRes, camRes, plaRes, creRes, catRes] = await Promise.all([
                assignmentsAPI.getAll(),
                campaignsAPI.getAll(),
                placementsAPI.getAll(),
                creativesAPI.getAll(),
                categoriesAPI.getAll()
            ]);

            setAssignments(assRes || []);
            setCampaigns(camRes || []);
            setPlacements(plaRes || []);
            setCreatives(creRes || []);
            setCategories(catRes || []);
        } catch (error) {
            console.error('Data initialization error:', error);
            toast.error('Failed to load ad data');
        } finally {
            setLoading(false);
        }
    };

    const handleOpenModal = (assignment = null) => {
        if (assignment) {
            setSelectedAssignment(assignment);
            setFormData({
                placementId: assignment.placementId,
                campaignId: assignment.campaignId,
                creativeId: assignment.creativeId || '',
                weight: assignment.weight || 50,
                startAt: assignment.startAt ? assignment.startAt.split('T')[0] : '',
                endAt: assignment.endAt ? assignment.endAt.split('T')[0] : '',
                langFilter: assignment.langFilter || [],
                categoryFilter: assignment.categoryFilter || [],
                isActive: assignment.isActive ?? true
            });
        } else {
            setSelectedAssignment(null);
            setFormData(initialFormState);
        }
        setIsModalOpen(true);
    };

    const handleSaveAssignment = async (e) => {
        e.preventDefault();
        setIsSaving(true);

        try {
            const payload = {
                ...formData,
                placementId: parseInt(formData.placementId),
                campaignId: parseInt(formData.campaignId),
                creativeId: formData.creativeId ? parseInt(formData.creativeId) : null,
                startAt: formData.startAt ? `${formData.startAt}T00:00:00` : null,
                endAt: formData.endAt ? `${formData.endAt}T23:59:59` : null
            };

            if (selectedAssignment) {
                await assignmentsAPI.update(selectedAssignment.id, payload);
                toast.success('Placement updated');
            } else {
                await assignmentsAPI.create(payload);
                toast.success('Ad placed successfully');
            }

            setIsModalOpen(false);
            initData(); // Refresh all
        } catch (error) {
            console.error('Error saving assignment:', error);
            toast.error(error.response?.data?.message || 'Failed to save placement');
        } finally {
            setIsSaving(false);
        }
    };

    const handleDelete = async () => {
        try {
            await assignmentsAPI.delete(selectedAssignment.id);
            toast.success('Placement removed');
            setIsDeleting(false);
            initData();
        } catch (error) {
            console.error('Removal error:', error);
            toast.error('Failed to remove placement');
        }
    };

    const filteredAssignments = assignments.filter(a =>
        a.placementCode?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        a.placementTitle?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        a.campaignName?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading && assignments.length === 0) return <PageLoader />;

    return (
        <div className="space-y-12 animate-fade-in pb-20">
            {/* Modern Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                    <div className="flex items-center gap-2 mb-2">
                        <div className="p-1.5 bg-indigo-50 text-indigo-600 rounded-lg">
                            <LayoutGrid size={14} />
                        </div>
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Advertising</span>
                    </div>
                    <h1 className="text-4xl font-black text-slate-900 tracking-tight font-heading">
                        Ad <span className="text-primary-600">Placements</span>
                    </h1>
                </div>

                <Button
                    variant="primary"
                    size="lg"
                    className="shadow-2xl shadow-primary-500/30 font-bold"
                    onClick={() => handleOpenModal()}
                >
                    <Plus size={20} className="mr-2" />
                    Place Ad
                </Button>
            </div>

            {/* Smart Search */}
            <div className="bg-white rounded-[2rem] p-4 border border-slate-100 shadow-xl shadow-slate-200/40 flex flex-col lg:flex-row items-center gap-4 max-w-4xl">
                <div className="relative flex-1 w-full group">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                        <Search size={18} className="text-slate-400 group-focus-within:text-primary-500 transition-colors" />
                    </div>
                    <input
                        type="text"
                        placeholder="Search by slot, campaign or code..."
                        className="w-full pl-11 pr-4 py-3 bg-slate-50 border-transparent rounded-[1.2rem] focus:outline-none focus:ring-4 focus:ring-primary-100 focus:bg-white transition-all text-sm font-medium"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
            </div>

            {/* Premium Table Content */}
            <div className="bg-white rounded-[2.5rem] border border-slate-100 shadow-[0_20px_60px_-15px_rgba(0,0,0,0.03)] overflow-hidden">
                <div className="overflow-x-auto">
                    <table className="w-full border-collapse">
                        <thead>
                            <tr className="bg-slate-50/50 border-b border-slate-100">
                                <th className="px-8 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Ad Slot</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Campaign</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Priority</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Status</th>
                                <th className="px-8 py-5 text-right text-[10px] font-bold uppercase tracking-wider text-slate-400">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-50">
                            {filteredAssignments.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="p-32 text-center">
                                        <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-300">
                                            <Layers size={32} />
                                        </div>
                                        <p className="text-slate-400 font-bold tracking-tight">No ads placed yet</p>
                                    </td>
                                </tr>
                            ) : (
                                filteredAssignments.map((assignment) => (
                                    <tr key={assignment.id} className="group hover:bg-slate-50/50 transition-colors">
                                        <td className="px-8 py-6">
                                            <div className="flex items-center gap-4">
                                                <div className="w-12 h-12 rounded-2xl bg-white border border-slate-100 flex items-center justify-center text-slate-400 group-hover:scale-110 group-hover:bg-indigo-50 group-hover:text-indigo-600 transition-all duration-300 shadow-sm">
                                                    <Target size={20} />
                                                </div>
                                                <div>
                                                    <div className="text-sm font-bold text-slate-900 mb-1 group-hover:text-primary-600 transition-colors">
                                                        {assignment.placementTitle || assignment.placementCode || 'Unknown Slot'}
                                                    </div>
                                                    <div className="flex items-center gap-2 text-[9px] font-bold text-slate-400 uppercase tracking-wider">
                                                        Code: <span className="text-primary-500 font-bold">{assignment.placementCode}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-6">
                                            <div className="flex items-center gap-2">
                                                <Target size={14} className="text-slate-300" />
                                                <span className="text-xs font-medium text-slate-600">{assignment.campaignName || 'Unassigned'}</span>
                                            </div>
                                        </td>
                                        <td className="px-6 py-6">
                                            <div className="flex items-center gap-3">
                                                <div className="flex-1 w-24 bg-slate-100 h-1.5 rounded-full overflow-hidden">
                                                    <div
                                                        className="h-full bg-primary-500 transition-all duration-1000"
                                                        style={{ width: `${assignment.weight || 10}%` }}
                                                    ></div>
                                                </div>
                                                <span className="text-[10px] font-bold text-slate-900">{assignment.weight || 0}%</span>
                                            </div>
                                        </td>
                                        <td className="px-6 py-6">
                                            <span className={`px-4 py-1.5 rounded-full text-[10px] font-black uppercase tracking-wider border ${assignment.isActive
                                                ? 'bg-emerald-50 text-emerald-600 border-emerald-100'
                                                : 'bg-slate-100 text-slate-400 border-slate-200'
                                                }`}>
                                                {assignment.isActive ? 'Active' : 'Inactive'}
                                            </span>
                                        </td>
                                        <td className="px-8 py-6 text-right">
                                            <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                                                <button
                                                    onClick={() => handleOpenModal(assignment)}
                                                    className="p-2.5 bg-white border border-slate-100 text-slate-400 hover:text-indigo-600 hover:border-indigo-100 rounded-xl transition-all shadow-sm"
                                                >
                                                    <Edit size={16} />
                                                </button>
                                                <button
                                                    onClick={() => { setSelectedAssignment(assignment); setIsDeleting(true); }}
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

            {/* Assignment Configuration Modal */}
            <Modal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                title={selectedAssignment ? "Edit Placement" : "Place Ad"}
                size="lg"
            >
                <form onSubmit={handleSaveAssignment} className="space-y-8">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <Select
                            label="Target Ad Slot"
                            value={formData.placementId}
                            onChange={(e) => setFormData({ ...formData, placementId: e.target.value })}
                            options={[
                                { value: '', label: 'Select a slot...' },
                                ...placements.map(p => ({ value: p.id, label: `${p.title} (${p.code})` }))
                            ]}
                            required
                        />
                        <Select
                            label="Active Campaign"
                            value={formData.campaignId}
                            onChange={(e) => setFormData({ ...formData, campaignId: e.target.value })}
                            options={[
                                { value: '', label: 'Select a campaign...' },
                                ...campaigns.map(c => ({ value: c.id, label: c.name }))
                            ]}
                            required
                        />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <Select
                            label="Ad Creative Logic"
                            value={formData.creativeId}
                            onChange={(e) => setFormData({ ...formData, creativeId: e.target.value })}
                            options={[
                                { value: '', label: 'Select creative unit...' },
                                ...creatives.map(c => ({ value: c.id, label: c.name }))
                            ]}
                            required
                        />
                        <div className="space-y-2">
                            <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider ml-1">Priority (%)</label>
                            <input
                                type="range"
                                min="0"
                                max="100"
                                step="5"
                                value={formData.weight}
                                onChange={(e) => setFormData({ ...formData, weight: parseInt(e.target.value) })}
                                className="w-full h-2 bg-slate-100 rounded-lg appearance-none cursor-pointer accent-primary-600"
                            />
                            <div className="flex justify-between text-[10px] font-bold text-slate-600">
                                <span>Low Priority</span>
                                <span className="text-primary-600">{formData.weight}%</span>
                                <span>High Priority</span>
                            </div>
                        </div>
                    </div>

                    <div className="bg-slate-50 p-6 rounded-[2rem] border border-slate-100 space-y-6">
                        <h4 className="flex items-center gap-2 text-[10px] font-bold uppercase tracking-wider text-slate-400">
                            <Globe size={12} /> Target Filters
                        </h4>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <div className="space-y-2">
                                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Language</label>
                                <div className="flex gap-2 flex-wrap">
                                    {['uz', 'ru', 'en'].map(lang => (
                                        <button
                                            key={lang}
                                            type="button"
                                            onClick={() => {
                                                const newLangs = formData.langFilter.includes(lang)
                                                    ? formData.langFilter.filter(l => l !== lang)
                                                    : [...formData.langFilter, lang];
                                                setFormData({ ...formData, langFilter: newLangs });
                                            }}
                                            className={`px-4 py-2 rounded-xl text-[10px] font-black uppercase transition-all border ${formData.langFilter.includes(lang)
                                                ? 'bg-primary-600 text-white border-primary-600 shadow-md shadow-primary-500/20'
                                                : 'bg-white text-slate-400 border-slate-100 hover:border-primary-200'
                                                }`}
                                        >
                                            {lang.toUpperCase()}
                                        </button>
                                    ))}
                                </div>
                            </div>
                            <div className="space-y-2">
                                <label className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Categories</label>
                                <div className="flex gap-2 flex-wrap max-h-24 overflow-y-auto p-1">
                                    {categories.map(cat => (
                                        <button
                                            key={cat.id}
                                            type="button"
                                            onClick={() => {
                                                const newCats = formData.categoryFilter.includes(cat.id)
                                                    ? formData.categoryFilter.filter(id => id !== cat.id)
                                                    : [...formData.categoryFilter, cat.id];
                                                setFormData({ ...formData, categoryFilter: newCats });
                                            }}
                                            className={`px-3 py-1.5 rounded-lg text-[9px] font-bold transition-all border ${formData.categoryFilter.includes(cat.id)
                                                ? 'bg-indigo-600 text-white border-indigo-600'
                                                : 'bg-white text-slate-500 border-slate-100'
                                                }`}
                                        >
                                            {cat.translationsDtoList?.[0]?.title}
                                        </button>
                                    ))}
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <Input
                            type="date"
                            label="Active From"
                            value={formData.startAt}
                            onChange={(e) => setFormData({ ...formData, startAt: e.target.value })}
                            required
                        />
                        <Input
                            type="date"
                            label="Active Until"
                            value={formData.endAt}
                            onChange={(e) => setFormData({ ...formData, endAt: e.target.value })}
                        />
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
                                    <Save size={18} /> {selectedAssignment ? "Save Changes" : "Place Ad"}
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
                title="Remove Placement"
                message="Are you sure you want to remove this ad from this slot? It will stop showing immediately."
                confirmText="Remove Ad"
                variant="danger"
            />
        </div>
    );
};
