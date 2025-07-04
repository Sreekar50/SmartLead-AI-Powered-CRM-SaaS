// src/services/dashboardService.ts
import axios from 'axios';

export interface DashboardStats {
  totalLeads: number;
  convertedLeads: number;
  conversionRate: number;
  avgLeadScore: number;
  leadsByStatus: {
    status: string;
    count: number;
  }[];
  leadsBySource: {
    source: string;
    count: number;
  }[];
  recentActivity: {
    id: number;
    leadName: string;
    action: string;
    timestamp: string;
    userName: string;
  }[];
  monthlyConversions: {
    month: string;
    conversions: number;
    revenue: number;
  }[];
}

class DashboardService {
  private getAuthHeaders() {
    const token = localStorage.getItem('token');
    return { Authorization: `Bearer ${token}` };
  }

  async getDashboardStats(): Promise<DashboardStats> {
    const response = await axios.get(`${process.env.REACT_APP_API_URL || 'http://localhost:8080/api'}/dashboard/stats`, {
      headers: this.getAuthHeaders()
    });
    return response.data;
  }
}

export const dashboardService = new DashboardService();