import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { roomAPI, codeAPI, submissionAPI, handleAPIError } from '../services/apiService';
import CodeEditor from './CodeEditor';
import TestCasePanel from './TestCasePanel';
import OutputPanel from './OutputPanel';

const Editor = () => {
  const { roomId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  
  // Get participant info from navigation state
  const participantName = location.state?.participantName || 'Anonymous';
  const isCreator = location.state?.isCreator || false;
  
  const [room, setRoom] = useState(null);
  const [loading, setLoading] = useState(true);
  const [code, setCode] = useState('');
  const [language, setLanguage] = useState('java');
  const [output, setOutput] = useState('');
  const [isRunning, setIsRunning] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [supportedLanguages, setSupportedLanguages] = useState(['java', 'python', 'cpp', 'c', 'javascript']);
  const [submissions, setSubmissions] = useState([]);
  const [lastResult, setLastResult] = useState(null);

  const loadRoomData = useCallback(async () => {
    try {
      const response = await roomAPI.getRoomDetails(roomId);
      if (response.success) {
        setRoom(response.data);
      } else {
        throw new Error(response.message || 'Failed to load room');
      }
    } catch (error) {
      const errorInfo = handleAPIError(error);
      if (errorInfo.status === 404) {
        toast.error('Room not found');
        navigate('/');
      } else {
        toast.error(errorInfo.message);
        console.error('Error loading room:', error);
      }
    } finally {
      setLoading(false);
    }
  }, [roomId, navigate]);

  const loadSupportedLanguages = useCallback(async () => {
    try {
      const response = await codeAPI.getSupportedLanguages();
      if (response.success && response.data) {
        setSupportedLanguages(response.data);
      }
    } catch (error) {
      console.error('Failed to load supported languages:', error);
      // Keep default languages if API fails
    }
  }, []);

  const loadCodeTemplate = useCallback(async () => {
    try {
      const response = await codeAPI.getCodeTemplate(language);
      if (response.success && response.data && !code.trim()) {
        setCode(response.data.template || '');
      }
    } catch (error) {
      console.error('Failed to load code template:', error);
    }
  }, [language, code]);

  useEffect(() => {
    if (!roomId) {
      toast.error('No room ID provided');
      navigate('/');
      return;
    }

    loadRoomData();
    loadSupportedLanguages();
  }, [roomId, navigate, loadRoomData, loadSupportedLanguages]);

  useEffect(() => {
    loadCodeTemplate();
  }, [loadCodeTemplate]);

  // Listen for custom run code event
  useEffect(() => {
    const handleRunCode = () => {
      runCode();
    };

    window.addEventListener('runCode', handleRunCode);
    return () => {
      window.removeEventListener('runCode', handleRunCode);
    };
  }, [code, language]); // Dependencies for runCode

  const runCode = async () => {
    if (!code.trim()) {
      toast.error('Please write some code first');
      return;
    }

    setIsRunning(true);
    setOutput('Running code...');

    try {
      const runRequest = {
        code: code.trim(),
        language,
        roomId
      };

      const response = await codeAPI.runCode(runRequest);
      
      if (response.success) {
        setLastResult(response.data);
        if (response.data.success) {
          setOutput(response.data.output || 'Code executed successfully');
        } else {
          setOutput(response.data.error || 'Execution failed');
        }
      } else {
        setOutput(`Error: ${response.message || 'Execution failed'}`);
      }
    } catch (error) {
      const errorInfo = handleAPIError(error);
      setOutput(`Error: ${errorInfo.message}`);
      toast.error(errorInfo.message);
    } finally {
      setIsRunning(false);
    }
  };

  const submitCode = async () => {
    if (!code.trim()) {
      toast.error('Please write some code first');
      return;
    }

    if (!participantName || participantName === 'Anonymous') {
      toast.error('Please provide your name to submit');
      return;
    }

    setIsSubmitting(true);

    try {
      const submitRequest = {
        roomId,
        participantName,
        code: code.trim(),
        language
      };

      const response = await submissionAPI.submitCode(submitRequest);
      
      if (response.success) {
        setLastResult(response.data);
        toast.success(response.message || 'Code submitted successfully!');
        
        // Update output with detailed results
        if (response.data.allTestCasesPassed) {
          setOutput('🎉 Congratulations! All test cases passed!');
        } else {
          setOutput(response.data.output || `${response.data.testCasesPassed}/${response.data.totalTestCases} test cases passed`);
        }
        
        // Reload submissions
        loadSubmissions();
      } else {
        throw new Error(response.message || 'Submission failed');
      }
    } catch (error) {
      const errorInfo = handleAPIError(error);
      setOutput(`Submission error: ${errorInfo.message}`);
      toast.error(errorInfo.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const loadSubmissions = async () => {
    try {
      const response = await submissionAPI.getSubmissionsByRoom(roomId);
      if (response.success) {
        setSubmissions(response.data || []);
      }
    } catch (error) {
      console.error('Failed to load submissions:', error);
    }
  };

  const handleCodeChange = useCallback((newCode) => {
    setCode(newCode);
  }, []);

  if (loading) {
    return (
      <div className="container" style={{ textAlign: 'center', paddingTop: '4rem' }}>
        <div className="spinner"></div>
        <p>Loading room...</p>
      </div>
    );
  }

  if (!room) {
    return (
      <div className="container" style={{ textAlign: 'center', paddingTop: '4rem' }}>
        <h2>Room not found</h2>
        <button className="btn btn-primary" onClick={() => navigate('/')}>
          Go Home
        </button>
      </div>
    );
  }

  return (
    <div style={{ height: '100vh', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <div style={{ 
        background: 'rgba(255, 255, 255, 0.95)', 
        padding: '1rem 2rem', 
        borderBottom: '1px solid #e0e0e0',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        flexShrink: 0
      }}>
        <div>
          <h3 style={{ margin: 0, color: '#333' }}>
            Room: {roomId} | Participant: {participantName}
            {isCreator && <span style={{ color: '#667eea', marginLeft: '10px' }}>(Creator)</span>}
          </h3>
        </div>
        <div style={{ display: 'flex', gap: '10px', alignItems: 'center' }}>
          <select
            value={language}
            onChange={(e) => setLanguage(e.target.value)}
            style={{ padding: '8px', borderRadius: '5px', border: '1px solid #ddd' }}
          >
            {supportedLanguages.map(lang => (
              <option key={lang} value={lang}>{lang.toUpperCase()}</option>
            ))}
          </select>
          <button
            className="btn btn-secondary"
            onClick={runCode}
            disabled={isRunning}
            style={{ minWidth: '80px' }}
          >
            {isRunning ? 'Running...' : 'Run'}
          </button>
          <button
            className="btn btn-success"
            onClick={submitCode}
            disabled={isSubmitting}
            style={{ minWidth: '100px' }}
          >
            {isSubmitting ? 'Submitting...' : 'Submit'}
          </button>
        </div>
      </div>

      {/* Main content */}
      <div className="editor-container" style={{ flex: 1, overflow: 'hidden' }}>
        {/* Left Panel - Question and Test Cases */}
        <div className="editor-panel">
          <div className="editor-header">
            Problem Statement
          </div>
          <div className="editor-content">
            <div style={{ marginBottom: '2rem' }}>
              <div style={{ whiteSpace: 'pre-wrap', lineHeight: '1.6' }}>
                {room.question}
              </div>
              {room.difficulty && (
                <div style={{ 
                  marginTop: '1rem', 
                  padding: '0.5rem 1rem', 
                  background: room.difficulty === 'easy' ? '#d4edda' : room.difficulty === 'medium' ? '#fff3cd' : '#f8d7da',
                  borderRadius: '5px',
                  display: 'inline-block'
                }}>
                  Difficulty: {room.difficulty.charAt(0).toUpperCase() + room.difficulty.slice(1)}
                </div>
              )}
            </div>
            
            <TestCasePanel testCases={room.testCases || []} />
          </div>
        </div>

        {/* Right Panel - Code Editor and Output */}
        <div className="editor-panel">
          <div className="editor-header">
            Code Editor ({language.toUpperCase()})
          </div>
          <div className="editor-content" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
            <div style={{ flex: '1', minHeight: '300px', marginBottom: '1rem' }}>
              <CodeEditor
                code={code}
                language={language}
                onChange={handleCodeChange}
              />
            </div>
            
            <OutputPanel
              output={output}
              result={lastResult}
              isRunning={isRunning || isSubmitting}
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default Editor;