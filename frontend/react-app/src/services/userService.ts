// src/services/userService.ts
import axios from 'axios';

export interface TeamMember {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  isActive: boolean;
}

class UserService {
  private getAuthHeaders() {
    const token = localStorage.getItem('token');
    return { Authorization: `Bearer ${token}` };
  }

  async getTeamMembers(): Promise<TeamMember[]> {
    const response = await axios.get(`${process.env.REACT_APP_API_URL || 'http://localhost:8080/api'}/users/team`, {
      headers: this.getAuthHeaders()
    });
    return response.data;
  }
}

export const userService = new UserService();