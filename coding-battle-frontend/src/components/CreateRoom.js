import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { roomAPI, handleAPIError } from '../services/apiService';

const CreateRoom = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    question: '',
    createdBy: '',
    difficulty: 'medium'
  });
  const [testCases, setTestCases] = useState([
    { input: '', expectedOutput: '', description: '', isHidden: false }
  ]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleTestCaseChange = (index, field, value) => {
    setTestCases(prev => {
      const updated = [...prev];
      updated[index] = { ...updated[index], [field]: value };
      return updated;
    });
  };

  const addTestCase = () => {
    setTestCases(prev => [
      ...prev,
      { input: '', expectedOutput: '', description: '', isHidden: false }
    ]);
  };

  const removeTestCase = (index) => {
    if (testCases.length > 1) {
      setTestCases(prev => prev.filter((_, i) => i !== index));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validation
    if (!formData.question.trim()) {
      toast.error('Please enter a question');
      return;
    }
    
    if (!formData.createdBy.trim()) {
      toast.error('Please enter your name');
      return;
    }

    const validTestCases = testCases.filter(tc => 
      tc.input.trim() && tc.expectedOutput.trim()
    );

    if (validTestCases.length === 0) {
      toast.error('Please add at least one test case with input and expected output');
      return;
    }

    setLoading(true);

    try {
      const roomData = {
        question: formData.question.trim(),
        createdBy: formData.createdBy.trim(),
        difficulty: formData.difficulty,
        testCases: validTestCases
      };

      const response = await roomAPI.createRoom(roomData);
      
      if (response.success) {
        toast.success(`Room created successfully! Room ID: ${response.data.roomId}`);
        // Navigate to editor with room ID
        navigate(`/editor/${response.data.roomId}`, {
          state: { 
            participantName: formData.createdBy,
            isCreator: true 
          }
        });
      }
    } catch (error) {
      const errorInfo = handleAPIError(error);
      toast.error(errorInfo.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="card fade-in">
        <h2 style={{ 
          marginBottom: '2rem', 
          color: '#333',
          textAlign: 'center'
        }}>
          Create Coding Room
        </h2>

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Your Name *</label>
            <input
              type="text"
              name="createdBy"
              className="form-control"
              value={formData.createdBy}
              onChange={handleInputChange}
              placeholder="Enter your name"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Difficulty Level</label>
            <select
              name="difficulty"
              className="form-control"
              value={formData.difficulty}
              onChange={handleInputChange}
            >
              <option value="easy">Easy</option>
              <option value="medium">Medium</option>
              <option value="hard">Hard</option>
            </select>
          </div>

          <div className="form-group">
            <label className="form-label">Problem Statement *</label>
            <textarea
              name="question"
              className="form-control"
              value={formData.question}
              onChange={handleInputChange}
              placeholder="Describe the coding problem here..."
              rows="6"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Test Cases *</label>
            {testCases.map((testCase, index) => (
              <div key={index} className="test-case" style={{ marginBottom: '1rem' }}>
                <div style={{ 
                  display: 'flex', 
                  justifyContent: 'space-between', 
                  alignItems: 'center',
                  marginBottom: '1rem'
                }}>
                  <h4>Test Case {index + 1}</h4>
                  {testCases.length > 1 && (
                    <button
                      type="button"
                      className="btn btn-danger"
                      style={{ padding: '5px 10px', fontSize: '0.8rem' }}
                      onClick={() => removeTestCase(index)}
                    >
                      Remove
                    </button>
                  )}
                </div>

                <div className="grid grid-2" style={{ marginBottom: '1rem' }}>
                  <div>
                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600' }}>
                      Input:
                    </label>
                    <textarea
                      className="form-control"
                      value={testCase.input}
                      onChange={(e) => handleTestCaseChange(index, 'input', e.target.value)}
                      placeholder="Input for this test case"
                      rows="3"
                    />
                  </div>
                  <div>
                    <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600' }}>
                      Expected Output:
                    </label>
                    <textarea
                      className="form-control"
                      value={testCase.expectedOutput}
                      onChange={(e) => handleTestCaseChange(index, 'expectedOutput', e.target.value)}
                      placeholder="Expected output for this test case"
                      rows="3"
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600' }}>
                    Description (Optional):
                  </label>
                  <input
                    type="text"
                    className="form-control"
                    value={testCase.description}
                    onChange={(e) => handleTestCaseChange(index, 'description', e.target.value)}
                    placeholder="Brief description of this test case"
                  />
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                  <input
                    type="checkbox"
                    checked={testCase.isHidden}
                    onChange={(e) => handleTestCaseChange(index, 'isHidden', e.target.checked)}
                  />
                  <label>Hidden test case (not visible during testing)</label>
                </div>
              </div>
            ))}

            <button
              type="button"
              className="btn btn-secondary"
              onClick={addTestCase}
              style={{ marginTop: '1rem' }}
            >
              + Add Test Case
            </button>
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
                  Creating...
                </>
              ) : (
                'Create Room'
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default CreateRoom;