import { useState, useEffect } from 'react';
import { Plus, Edit, Trash2, Megaphone, Calendar, Users, Activity, ExternalLink, Filter, Search, Save, X, DollarSign, Target } from 'lucide-react';
import toast from 'react-hot-toast';
import { campaignsAPI } from '../../api/campaigns';
import { Button } from '../../components/common/Button';
import { Card } from '../../components/common/Card';
import { PageLoader } from '../../components/common/Loading';
import { Modal, ConfirmModal } from '../../components/common/Modal';
import { Input, Select } from '../../components/common/FormFields';

export const CampaignsPage = () => {
    const [campaigns, setCampaigns] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState('');

    // Modal states
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [selectedCampaign, setSelectedCampaign] = useState(null);
    const [isSaving, setIsSaving] = useState(false);

    const initialFormState = {
        name: '',
        advertiser: '',
        startAt: '',
        endAt: '',
        dailyCapImpressions: 1000,
        dailyCapClicks: 100
    };

    const [formData, setFormData] = useState(initialFormState);

    useEffect(() => {
        fetchCampaigns();
    }, []);

    const fetchCampaigns = async () => {
        try {
            setLoading(true);
            const response = await campaignsAPI.getAll();
            setCampaigns(response.data || []);
        } catch (error) {
            console.error('Error fetching campaigns:', error);
            toast.error('Failed to load campaigns');
        } finally {
            setLoading(false);
        }
    };

    const handleOpenModal = (campaign = null) => {
        if (campaign) {
            setSelectedCampaign(campaign);
            setFormData({
                name: campaign.name,
                advertiser: campaign.advertiser || '',
                startAt: campaign.startAt ? campaign.startAt.split('T')[0] : '',
                endAt: campaign.endAt ? campaign.endAt.split('T')[0] : '',
                dailyCapImpressions: campaign.dailyCapImpressions || 1000,
                dailyCapClicks: campaign.dailyCapClicks || 100
            });
        } else {
            setSelectedCampaign(null);
            setFormData(initialFormState);
        }
        setIsModalOpen(true);
    };

    const handleSaveCampaign = async (e) => {
        e.preventDefault();
        setIsSaving(true);

        try {
            // Backend expects ISO LocalDateTime. Adding time component.
            const payload = {
                ...formData,
                startAt: formData.startAt ? `${formData.startAt}T00:00:00` : null,
                endAt: formData.endAt ? `${formData.endAt}T23:59:59` : null
            };

            if (selectedCampaign) {
                await campaignsAPI.update(selectedCampaign.id, payload);
                toast.success('Campaign updated');
            } else {
                await campaignsAPI.create(payload);
                toast.success('Campaign created');
            }

            setIsModalOpen(false);
            fetchCampaigns();
        } catch (error) {
            console.error('Error saving campaign:', error);
            toast.error(error.response?.data?.message || 'Failed to save campaign');
        } finally {
            setIsSaving(false);
        }
    };

    const handleUpdateStatus = async (id, status) => {
        try {
            await campaignsAPI.updateStatus(id, { status });
            toast.success(`Status updated: ${status}`);
            fetchCampaigns();
        } catch (error) {
            console.error('Status error:', error);
            toast.error('Failed to update status');
        }
    };

    const handleDelete = async () => {
        try {
            await campaignsAPI.delete(selectedCampaign.id);
            toast.success('Campaign deleted');
            setIsDeleting(false);
            fetchCampaigns();
        } catch (error) {
            console.error('Delete error:', error);
            toast.error('Failed to delete campaign');
        }
    };

    const getStatusStyle = (status) => {
        const styles = {
            DRAFT: 'bg-slate-100 text-slate-500 border-slate-200',
            ACTIVE: 'bg-emerald-50 text-emerald-600 border-emerald-100',
            PAUSED: 'bg-amber-50 text-amber-600 border-amber-100',
            ENDED: 'bg-rose-50 text-rose-600 border-rose-100',
        };
        return styles[status] || styles.DRAFT;
    };

    const filteredCampaigns = campaigns.filter(c =>
        c.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        c.advertiser?.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading && campaigns.length === 0) return <PageLoader />;

    return (
        <div className="space-y-12 animate-fade-in pb-20">
            {/* Modern Header */}
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div>
                    <div className="flex items-center gap-2 mb-2">
                        <div className="p-1.5 bg-rose-50 text-rose-600 rounded-lg">
                            <Megaphone size={14} />
                        </div>
                        <span className="text-[10px] font-bold uppercase tracking-wider text-slate-400">Advertising</span>
                    </div>
                    <h1 className="text-4xl font-black text-slate-900 tracking-tight font-heading">
                        Manage <span className="text-primary-600">Campaigns</span>
                    </h1>
                </div>

                <Button
                    variant="primary"
                    size="lg"
                    className="shadow-2xl shadow-primary-500/30 font-bold"
                    onClick={() => handleOpenModal()}
                >
                    <Plus size={20} className="mr-2" />
                    Create Campaign
                </Button>
            </div>

            {/* Smart Toolbar */}
            <div className="bg-white rounded-[2rem] p-4 border border-slate-100 shadow-xl shadow-slate-200/40 flex flex-col lg:flex-row items-center gap-4">
                <div className="relative flex-1 w-full group">
                    <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
                        <Search size={18} className="text-slate-400 group-focus-within:text-primary-500 transition-colors" />
                    </div>
                    <input
                        type="text"
                        placeholder="Search by name or advertiser..."
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
                                <th className="px-8 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Campaign</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Advertiser</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Status</th>
                                <th className="px-6 py-5 text-left text-[10px] font-bold uppercase tracking-wider text-slate-400">Dates</th>
                                <th className="px-8 py-5 text-right text-[10px] font-bold uppercase tracking-wider text-slate-400">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-slate-50">
                            {filteredCampaigns.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="p-32 text-center">
                                        <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-300">
                                            <Megaphone size={32} />
                                        </div>
                                        <p className="text-slate-400 font-bold tracking-tight">No campaigns found</p>
                                    </td>
                                </tr>
                            ) : (
                                filteredCampaigns.map((campaign) => (
                                    <tr key={campaign.id} className="group hover:bg-slate-50/50 transition-colors">
                                        <td className="px-8 py-6">
                                            <div className="flex items-center gap-4">
                                                <div className="w-12 h-12 rounded-2xl bg-white border border-slate-100 flex items-center justify-center text-slate-400 group-hover:scale-110 group-hover:bg-primary-50 group-hover:text-primary-600 transition-all duration-300 shadow-sm">
                                                    <Megaphone size={20} />
                                                </div>
                                                <div>
                                                    <div className="text-sm font-bold text-slate-900 mb-1 group-hover:text-primary-600 transition-colors">{campaign.name}</div>
                                                    <div className="flex items-center gap-2">
                                                        <span className="text-[9px] font-bold text-slate-400 uppercase tracking-wider">ID: #{campaign.id}</span>
                                                        <div className="w-1 h-1 rounded-full bg-slate-200"></div>
                                                        <span className="text-[9px] font-bold text-primary-500 uppercase tracking-wider">CPM Model</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-6">
                                            <div className="flex items-center gap-2">
                                                <Users size={14} className="text-slate-300" />
                                                <span className="text-xs font-medium text-slate-600">{campaign.advertiser || 'Default'}</span>
                                            </div>
                                        </td>
                                        <td className="px-6 py-6">
                                            <span className={`px-4 py-1.5 rounded-full text-[10px] font-black uppercase tracking-wider border ${getStatusStyle(campaign.status)}`}>
                                                {campaign.status}
                                            </span>
                                        </td>
                                        <td className="px-6 py-6">
                                            <div className="flex flex-col gap-1">
                                                <div className="flex items-center text-[10px] font-bold text-slate-400">
                                                    <Calendar size={12} className="mr-1.5" />
                                                    {campaign.startAt ? new Date(campaign.startAt).toLocaleDateString() : 'N/A'}
                                                    <span className="mx-1">→</span>
                                                    {campaign.endAt ? new Date(campaign.endAt).toLocaleDateString() : 'N/A'}
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-8 py-6 text-right">
                                            <div className="flex justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                                                <button
                                                    onClick={() => handleOpenModal(campaign)}
                                                    className="p-2.5 bg-white border border-slate-100 text-slate-400 hover:text-indigo-600 hover:border-indigo-100 rounded-xl transition-all shadow-sm"
                                                >
                                                    <Edit size={16} />
                                                </button>
                                                <button
                                                    onClick={() => { setSelectedCampaign(campaign); setIsDeleting(true); }}
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

            {/* Campaign Creation/Edit Modal */}
            <Modal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                title={selectedCampaign ? "Edit Campaign" : "Create Campaign"}
                size="lg"
            >
                <form onSubmit={handleSaveCampaign} className="space-y-8">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <Input
                            label="Campaign Name"
                            placeholder="e.g. Summer Tech Expo 2026"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            required
                        />
                        <Input
                            label="Advertiser"
                            placeholder="e.g. Acme Corp"
                            value={formData.advertiser}
                            onChange={(e) => setFormData({ ...formData, advertiser: e.target.value })}
                            required
                        />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <Input
                            type="date"
                            label="Start Date"
                            value={formData.startAt}
                            onChange={(e) => setFormData({ ...formData, startAt: e.target.value })}
                            required
                        />
                        <Input
                            type="date"
                            label="End Date"
                            value={formData.endAt}
                            onChange={(e) => setFormData({ ...formData, endAt: e.target.value })}
                        />
                    </div>

                    <div className="bg-slate-50 p-6 rounded-[2rem] border border-slate-100 space-y-6">
                        <h4 className="flex items-center gap-2 text-[10px] font-bold uppercase tracking-wider text-slate-400">
                            <Activity size={12} /> Delivery Limits
                        </h4>
                        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                            <Input
                                type="number"
                                label="Daily Impression Cap"
                                value={formData.dailyCapImpressions}
                                onChange={(e) => setFormData({ ...formData, dailyCapImpressions: parseInt(e.target.value) })}
                                icon={<Target size={16} />}
                            />
                            <Input
                                type="number"
                                label="Daily Clicks Cap"
                                value={formData.dailyCapClicks}
                                onChange={(e) => setFormData({ ...formData, dailyCapClicks: parseInt(e.target.value) })}
                                icon={<Activity size={16} />}
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
                                    <Save size={18} /> {selectedCampaign ? "Save Changes" : "Create Campaign"}
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
                title="Delete Campaign"
                message="Are you sure you want to delete this campaign? This will stop all associated ads."
                confirmText="Delete Campaign"
                variant="danger"
            />
        </div>
    );
};
