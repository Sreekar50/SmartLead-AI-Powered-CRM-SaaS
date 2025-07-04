// src/components/LeadDetail.tsx
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Layout from './Layout';
import { leadService, Lead, Interaction } from '../services/leadService';
import { userService, TeamMember } from '../services/userService';
import LoadingSpinner from './LoadingSpinner';
import InteractionForm from './InteractionForm';

const LeadDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [lead, setLead] = useState<Lead | null>(null);
  const [teamMembers, setTeamMembers] = useState<TeamMember[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [editing, setEditing] = useState(false);
  const [showInteractionForm, setShowInteractionForm] = useState(false);

  useEffect(() => {
    if (id) {
      fetchLead();
      fetchTeamMembers();
    }
  }, [id]);

  const fetchLead = async () => {
    try {
      setLoading(true);
      const data = await leadService.getLeadById(Number(id));
      setLead(data);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to load lead');
    } finally {
      setLoading(false);
    }
  };

  const fetchTeamMembers = async () => {
    try {
      const data = await userService.getTeamMembers();
      setTeamMembers(data);
    } catch (err) {
      console.error('Failed to load team members:', err);
    }
  };

  const handleStatusChange = async (newStatus: Lead['status']) => {
    if (!lead) return;

    try {
      const updatedLead = await leadService.updateLead(lead.id, { status: newStatus });
      setLead(updatedLead);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update status');
    }
  };

  const handleAssignmentChange = async (assignedTo: number) => {
    if (!lead) return;

    try {
      const updatedLead = await leadService.updateLead(lead.id, { assignedTo });
      setLead(updatedLead);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to update assignment');
    }
  };

  const handleInteractionAdded = () => {
    setShowInteractionForm(false);
    fetchLead(); // Refresh to get updated interactions
  };

  const handleDeleteLead = async () => {
    if (!lead || !window.confirm('Are you sure you want to delete this lead?')) return;

    try {
      await leadService.deleteLead(lead.id);
      navigate('/leads');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Failed to delete lead');
    }
  };

  const getStatusBadgeColor = (status: Lead['status']) => {
    switch (status) {
      case 'NEW': return 'bg-blue-100 text-blue-800';
      case 'CONTACTED': return 'bg-yellow-100 text-yellow-800';
      case 'QUALIFIED': return 'bg-green-100 text-green-800';
      case 'CONVERTED': return 'bg-purple-100 text-purple-800';
      case 'LOST': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getScoreColor = (score: number) => {
    if (score >= 80) return 'text-green-600';
    if (score >= 60) return 'text-yellow-600';
    return 'text-red-600';
  };

  const getInteractionIcon = (type: Interaction['type']) => {
    switch (type) {
      case 'EMAIL': return '📧';
      case 'CALL': return '📞';
      case 'MEETING': return '🤝';
      case 'NOTE': return '📝';
      default: return '💬';
    }
  };

  if (loading) return <Layout><LoadingSpinner /></Layout>;
  if (error) return <Layout><div className="text-red-600 text-center">{error}</div></Layout>;
  if (!lead) return <Layout><div className="text-center">Lead not found</div></Layout>;

  return (
    <Layout>
      <div className="px-4 py-6 sm:px-0">
        <div className="flex justify-between items-center mb-8">
          <div>
            <button
              onClick={() => navigate('/leads')}
              className="text-primary-600 hover:text-primary-800 mb-2"
            >
              ← Back to Leads
            </button>
            <h1 className="text-2xl font-bold text-gray-900">
              {lead.firstName} {lead.lastName}
            </h1>
          </div>
          <div className="flex space-x-3">
            <button
              onClick={() => setShowInteractionForm(true)}
              className="bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-md text-sm font-medium"
            >
              Add Interaction
            </button>
            <button
              onClick={handleDeleteLead}
              className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-md text-sm font-medium"
            >
              Delete Lead
            </button>
          </div>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Lead Information */}
          <div className="lg:col-span-2">
            <div className="bg-white shadow rounded-lg">
              <div className="px-6 py-4 border-b border-gray-200">
                <h3 className="text-lg font-medium text-gray-900">Lead Information</h3>
              </div>
              <div className="px-6 py-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Email</label>
                    <p className="mt-1 text-sm text-gray-900">{lead.email}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Phone</label>
                    <p className="mt-1 text-sm text-gray-900">{lead.phone || '-'}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Company</label>
                    <p className="mt-1 text-sm text-gray-900">{lead.company || '-'}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Title</label>
                    <p className="mt-1 text-sm text-gray-900">{lead.title || '-'}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Source</label>
                    <p className="mt-1 text-sm text-gray-900">{lead.source}</p>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700">Created</label>
                    <p className="mt-1 text-sm text-gray-900">
                      {new Date(lead.createdAt).toLocaleDateString()}
                    </p>
                  </div>
                </div>
                {lead.notes && (
                  <div className="mt-4">
                    <label className="block text-sm font-medium text-gray-700">Notes</label>
                    <p className="mt-1 text-sm text-gray-900 whitespace-pre-wrap">{lead.notes}</p>
                  </div>
                )}
              </div>
            </div>

            {/* Interactions */}
            <div className="bg-white shadow rounded-lg mt-6">
              <div className="px-6 py-4 border-b border-gray-200">
                <h3 className="text-lg font-medium text-gray-900">Interactions</h3>
              </div>
              <div className="divide-y divide-gray-200">
                {lead.interactions && lead.interactions.length > 0 ? (
                  lead.interactions.map((interaction) => (
                    <div key={interaction.id} className="px-6 py-4">
                      <div className="flex items-start space-x-3">
                        <div className="text-2xl">{getInteractionIcon(interaction.type)}</div>
                        <div className="flex-1">
                          <div className="flex items-center justify-between">
                            <div>
                              <h4 className="text-sm font-medium text-gray-900">
                                {interaction.subject || interaction.type}
                              </h4>
                              <p className="text-sm text-gray-500">
                                by {interaction.userFirstName} {interaction.userLastName}
                              </p>
                            </div>
                            <p className="text-sm text-gray-500">
                              {new Date(interaction.createdAt).toLocaleString()}
                            </p>
                          </div>
                          <p className="mt-2 text-sm text-gray-700 whitespace-pre-wrap">
                            {interaction.content}
                          </p>
                        </div>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="px-6 py-4 text-center text-gray-500">
                    No interactions yet
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Status and Assignment */}
          <div className="space-y-6">
            <div className="bg-white shadow rounded-lg">
              <div className="px-6 py-4 border-b border-gray-200">
                <h3 className="text-lg font-medium text-gray-900">Status</h3>
              </div>
              <div className="px-6 py-4">
                <div className="mb-4">
                  <span className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getStatusBadgeColor(lead.status)}`}>
                    {lead.status}
                  </span>
                </div>
                <select
                  value={lead.status}
                  onChange={(e) => handleStatusChange(e.target.value as Lead['status'])}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                >
                  <option value="NEW">New</option>
                  <option value="CONTACTED">Contacted</option>
                  <option value="QUALIFIED">Qualified</option>
                  <option value="CONVERTED">Converted</option>
                  <option value="LOST">Lost</option>
                </select>
              </div>
            </div>

            <div className="bg-white shadow rounded-lg">
              <div className="px-6 py-4 border-b border-gray-200">
                <h3 className="text-lg font-medium text-gray-900">Lead Score</h3>
              </div>
              <div className="px-6 py-4 text-center">
                <div className={`text-3xl font-bold ${getScoreColor(lead.score)}`}>
                  {lead.score}/100
                </div>
                <div className="mt-2 bg-gray-200 rounded-full h-2">
                  <div
                    className={`h-2 rounded-full ${
                      lead.score >= 80 ? 'bg-green-500' :
                      lead.score >= 60 ? 'bg-yellow-500' : 'bg-red-500'
                    }`}
                    style={{ width: `${lead.score}%` }}
                  ></div>
                </div>
              </div>
            </div>

            <div className="bg-white shadow rounded-lg">
              <div className="px-6 py-4 border-b border-gray-200">
                <h3 className="text-lg font-medium text-gray-900">Assignment</h3>
              </div>
              <div className="px-6 py-4">
                <select
                  value={lead.assignedTo || ''}
                  onChange={(e) => handleAssignmentChange(Number(e.target.value))}
                  className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary-500 focus:border-primary-500"
                >
                  <option value="">Unassigned</option>
                  {teamMembers.map(member => (
                    <option key={member.id} value={member.id}>
                      {member.firstName} {member.lastName}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Interaction Form Modal */}
      {showInteractionForm && (
        <InteractionForm
          leadId={lead.id}
          onClose={() => setShowInteractionForm(false)}
          onInteractionAdded={handleInteractionAdded}
        />
      )}
    </Layout>
  );
};

export default LeadDetail;