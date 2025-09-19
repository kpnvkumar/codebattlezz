import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 30000, // 30 seconds timeout
});

// Request interceptor
api.interceptors.request.use(
  (config) => {
    console.log('API Request:', config.method?.toUpperCase(), config.url, config.data);
    return config;
  },
  (error) => {
    console.error('Request Error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor
api.interceptors.response.use(
  (response) => {
    console.log('API Response:', response.status, response.data);
    return response;
  },
  (error) => {
    console.error('Response Error:', error.response?.status, error.response?.data || error.message);
    return Promise.reject(error);
  }
);

// Room API calls
export const roomAPI = {
  createRoom: async (roomData) => {
    const response = await api.post('/rooms/create', roomData);
    return response.data;
  },

  joinRoom: async (joinData) => {
    const response = await api.post('/rooms/join', joinData);
    return response.data;
  },

  getRoomDetails: async (roomId) => {
    const response = await api.get(`/rooms/${roomId}`);
    return response.data;
  },

  validateRoom: async (roomId) => {
    const response = await api.get(`/rooms/${roomId}/validate`);
    return response.data;
  },

  getActiveRooms: async () => {
    const response = await api.get('/rooms/active');
    return response.data;
  },

  getRoomsByCreator: async (createdBy) => {
    const response = await api.get(`/rooms/by-creator/${createdBy}`);
    return response.data;
  },

  deactivateRoom: async (roomId) => {
    const response = await api.put(`/rooms/${roomId}/deactivate`);
    return response.data;
  },

  deleteRoom: async (roomId) => {
    const response = await api.delete(`/rooms/${roomId}`);
    return response.data;
  }
};

// Code execution API calls
export const codeAPI = {
  runCode: async (codeData) => {
    const response = await api.post('/code/run', codeData);
    return response.data;
  },

  getSupportedLanguages: async () => {
    const response = await api.get('/code/languages');
    return response.data;
  },

  getCodeTemplate: async (language) => {
    const response = await api.get(`/code/template/${language}`);
    return response.data;
  },

  validateLanguage: async (language) => {
    const response = await api.get(`/code/language/${language}/validate`);
    return response.data;
  }
};

// Submission API calls
export const submissionAPI = {
  submitCode: async (submissionData) => {
    const response = await api.post('/submissions/submit', submissionData);
    return response.data;
  },

  getSubmissionsByRoom: async (roomId) => {
    const response = await api.get(`/submissions/room/${roomId}`);
    return response.data;
  },

  getSubmissionsByRoomAndParticipant: async (roomId, participantName) => {
    const response = await api.get(`/submissions/room/${roomId}/participant/${participantName}`);
    return response.data;
  },

  getAcceptedSubmissions: async (roomId) => {
    const response = await api.get(`/submissions/room/${roomId}/accepted`);
    return response.data;
  }
};

// Error handler utility
export const handleAPIError = (error) => {
  if (error.response) {
    // Server responded with error status
    const errorData = error.response.data;
    return {
      message: errorData.message || 'An error occurred',
      error: errorData.error || error.message,
      status: error.response.status
    };
  } else if (error.request) {
    // Network error
    return {
      message: 'Network error - please check your connection',
      error: 'Network Error',
      status: 0
    };
  } else {
    // Other error
    return {
      message: error.message || 'An unexpected error occurred',
      error: 'Unknown Error',
      status: -1
    };
  }
};

export default api;