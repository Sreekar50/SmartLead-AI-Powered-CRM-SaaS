// src/services/leadService.ts
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export interface Lead {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  company?: string;
  title?: string;
  status: 'NEW' | 'CONTACTED' | 'QUALIFIED' | 'CONVERTED' | 'LOST';
  score: number;
  source: string;
  notes?: string;
  createdAt: string;
  updatedAt: string;
  assignedTo?: number;
  interactions?: Interaction[];
}

export interface Interaction {
  id: number;
  leadId: number;
  userId: number;
  type: 'EMAIL' | 'CALL' | 'MEETING' | 'NOTE';
  subject?: string;
  content: string;
  createdAt: string;
  userFirstName: string;
  userLastName: string;
}

export interface CreateLeadRequest {
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  company?: string;
  title?: string;
  source: string;
  notes?: string;
}

export interface UpdateLeadRequest extends Partial<CreateLeadRequest> {
  status?: Lead['status'];
  assignedTo?: number;
}

export interface LeadFilters {
  status?: string;
  assignedTo?: number;
  source?: string;
  minScore?: number;
  maxScore?: number;
  search?: string;
}

class LeadService {
  private getAuthHeaders() {
    const token = localStorage.getItem('token');
    return { Authorization: `Bearer ${token}` };
  }

  async getLeads(filters?: LeadFilters): Promise<Lead[]> {
    const params = new URLSearchParams();
    if (filters) {
      Object.entries(filters).forEach(([key, value]) => {
        if (value !== undefined && value !== '') {
          params.append(key, value.toString());
        }
      });
    }

    const response = await axios.get(`${API_BASE_URL}/leads?${params}`, {
      headers: this.getAuthHeaders()
    });
    return response.data;
  }

  async getLeadById(id: number): Promise<Lead> {
    const response = await axios.get(`${API_BASE_URL}/leads/${id}`, {
      headers: this.getAuthHeaders()
    });
    return response.data;
  }

  async createLead(lead: CreateLeadRequest): Promise<Lead> {
    const response = await axios.post(`${API_BASE_URL}/leads`, lead, {
      headers: this.getAuthHeaders()
    });
    return response.data;
  }

  async updateLead(id: number, lead: UpdateLeadRequest): Promise<Lead> {
    const response = await axios.put(`${API_BASE_URL}/leads/${id}`, lead, {
      headers: this.getAuthHeaders()
    });
    return response.data;
  }

  async deleteLead(id: number): Promise<void> {
    await axios.delete(`${API_BASE_URL}/leads/${id}`, {
      headers: this.getAuthHeaders()
    });
  }

  async addInteraction(leadId: number, interaction: {
    type: Interaction['type'];
    subject?: string;
    content: string;
  }): Promise<Interaction> {
    const response = await axios.post(
      `${API_BASE_URL}/leads/${leadId}/interactions`,
      interaction,
      { headers: this.getAuthHeaders() }
    );
    return response.data;
  }

  async importLeads(file: File): Promise<{ success: number; failed: number }> {
    const formData = new FormData();
    formData.append('file', file);

    const response = await axios.post(`${API_BASE_URL}/leads/import`, formData, {
      headers: {
        ...this.getAuthHeaders(),
        'Content-Type': 'multipart/form-data'
      }
    });
    return response.data;
  }
}

export const leadService = new LeadService();