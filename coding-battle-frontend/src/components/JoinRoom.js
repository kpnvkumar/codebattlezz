import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { roomAPI, handleAPIError } from '../services/apiService';

const JoinRoom = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    roomId: '',
    participantName: ''
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value.toUpperCase() // Room IDs are typically uppercase
    }));
  };

  const handleNameChange = (e) => {
    setFormData(prev => ({
      ...prev,
      participantName: e.target.value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.roomId.trim()) {
      toast.error('Please enter a room ID');
      return;
    }
    
    if (!formData.participantName.trim()) {
      toast.error('Please enter your name');
      return;
    }

    setLoading(true);

    try {
      const joinData = {
        roomId: formData.roomId.trim(),
        participantName: formData.participantName.trim()
      };

      const response = await roomAPI.joinRoom(joinData);
      
      if (response.success) {
        toast.success('Successfully joined the room!');
        // Navigate to editor with room ID and participant info
        navigate(`/editor/${formData.roomId}`, {
          state: { 
            participantName: formData.participantName,
            isCreator: false 
          }
        });
      }
    } catch (error) {
      const errorInfo = handleAPIError(error);
      if (errorInfo.status === 404) {
        toast.error('Room not found. Please check the room ID.');
      } else {
        toast.error(errorInfo.message);
      }
    } finally {
      setLoading(false);
    }
  };

  const validateRoomId = async () => {
    if (!formData.roomId.trim()) {
      toast.error('Please enter a room ID first');
      return;
    }

    try {
      const response = await roomAPI.validateRoom(formData.roomId.trim());
      if (response.success && response.data.valid) {
        toast.success('Room ID is valid!');
      } else {
        toast.error('Invalid room ID');
      }
    } catch (error) {
      const errorInfo = handleAPIError(error);
      toast.error(errorInfo.message);
    }
  };

  return (
    <div className="container">
      <div className="card fade-in" style={{ maxWidth: '500px', margin: '2rem auto' }}>
        <h2 style={{ 
          marginBottom: '2rem', 
          color: '#333',
          textAlign: 'center'
        }}>
          Join Coding Room
        </h2>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Room ID *</label>
            <div style={{ display: 'flex', gap: '10px' }}>
              <input
                type="text"
                name="roomId"
                className="form-control"
                value={formData.roomId}
                onChange={handleInputChange}
                placeholder="Enter room ID (e.g., ABC123)"
                maxLength="10"
                style={{ 
                  fontFamily: 'monospace', 
                  fontSize: '1.1rem',
                  letterSpacing: '1px'
                }}
                required
              />
              <button
                type="button"
                className="btn btn-secondary"
                onClick={validateRoomId}
                style={{ minWidth: '100px' }}
              >
                Verify
              </button>
            </div>
            <small style={{ color: '#666', marginTop: '5px', display: 'block' }}>
              Enter the room ID provided by the room creator
            </small>
          </div>

          <div className="form-group">
            <label className="form-label">Your Name *</label>
            <input
              type="text"
              name="participantName"
              className="form-control"
              value={formData.participantName}
              onChange={handleNameChange}
              placeholder="Enter your name"
              required
            />
          </div>

          <div style={{ textAlign: 'center', marginTop: '2rem' }}>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
              style={{ minWidth: '200px' }}
            >
              {loading ? (
                <>
                  <div className="spinner" style={{ 
                    width: '16px', 
                    height: '16px', 
                    margin: '0 10px 0 0',
                    display: 'inline-block'
                  }}></div>
                  Joining...
                </>
              ) : (
                'Join Room'
              )}
            </button>
          </div>
        </form>

        <div style={{ 
          marginTop: '3rem', 
          padding: '1.5rem', 
          background: 'rgba(102, 126, 234, 0.1)', 
          borderRadius: '10px',
          textAlign: 'left'
        }}>
          <h4 style={{ color: '#333', marginBottom: '1rem' }}>How to Join:</h4>
          <ol style={{ color: '#666', paddingLeft: '1.5rem' }}>
            <li style={{ marginBottom: '0.5rem' }}>
              Get the room ID from the person who created the room
            </li>
            <li style={{ marginBottom: '0.5rem' }}>
              Enter the room ID in the field above
            </li>
            <li style={{ marginBottom: '0.5rem' }}>
              Click "Verify" to check if the room exists (optional)
            </li>
            <li style={{ marginBottom: '0.5rem' }}>
              Enter your name and click "Join Room"
            </li>
          </ol>
        </div>
      </div>
    </div>
  );
};

export default JoinRoom;