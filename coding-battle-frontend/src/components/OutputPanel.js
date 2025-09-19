import React, { useState } from 'react';

const OutputPanel = ({ output, result, isRunning }) => {
  const [activeTab, setActiveTab] = useState('output');

  const getOutputClass = () => {
    if (isRunning) return '';
    if (result && result.success) return 'output-success';
    if (result && !result.success) return 'output-error';
    return '';
  };

  const renderTestCaseResults = () => {
    if (!result || !result.testCaseResults) {
      return <div>No detailed test case results available.</div>;
    }

    return (
      <div>
        <div style={{ 
          marginBottom: '1rem',
          padding: '1rem',
          background: result.allTestCasesPassed ? '#d4edda' : '#f8d7da',
          borderRadius: '5px',
          border: `1px solid ${result.allTestCasesPassed ? '#c3e6cb' : '#f5c6cb'}`
        }}>
          <strong>
            {result.allTestCasesPassed ? '✅ All Test Cases Passed!' : 
             `❌ ${result.testCasesPassed}/${result.totalTestCases} Test Cases Passed`}
          </strong>
          {result.executionTime && (
            <div style={{ fontSize: '0.9rem', marginTop: '0.5rem', color: '#666' }}>
              Execution Time: {result.executionTime}ms
            </div>
          )}
        </div>

        {result.testCaseResults.map((tcResult, index) => (
          <div 
            key={index} 
            style={{ 
              marginBottom: '1rem',
              padding: '1rem',
              border: `1px solid ${tcResult.passed ? '#c3e6cb' : '#f5c6cb'}`,
              borderRadius: '5px',
              background: tcResult.passed ? '#f8fff9' : '#fff5f5'
            }}
          >
            <div style={{ 
              display: 'flex', 
              justifyContent: 'space-between', 
              alignItems: 'center',
              marginBottom: '0.5rem'
            }}>
              <strong>Test Case {index + 1}</strong>
              <span style={{ 
                color: tcResult.passed ? '#28a745' : '#dc3545',
                fontWeight: 'bold'
              }}>
                {tcResult.passed ? '✅ PASSED' : '❌ FAILED'}
              </span>
            </div>

            <div style={{ fontSize: '0.85rem', fontFamily: 'monospace' }}>
              <div style={{ marginBottom: '0.5rem' }}>
                <strong>Input:</strong>
                <div style={{ 
                  background: '#f8f9fa', 
                  padding: '0.5rem', 
                  marginTop: '0.25rem',
                  borderRadius: '3px',
                  whiteSpace: 'pre-wrap'
                }}>
                  {tcResult.input || '(empty)'}
                </div>
              </div>

              <div style={{ marginBottom: '0.5rem' }}>
                <strong>Expected:</strong>
                <div style={{ 
                  background: '#f8f9fa', 
                  padding: '0.5rem', 
                  marginTop: '0.25rem',
                  borderRadius: '3px',
                  whiteSpace: 'pre-wrap'
                }}>
                  {tcResult.expectedOutput || '(empty)'}
                </div>
              </div>

              <div style={{ marginBottom: '0.5rem' }}>
                <strong>Actual:</strong>
                <div style={{ 
                  background: tcResult.passed ? '#f8f9fa' : '#ffe6e6', 
                  padding: '0.5rem', 
                  marginTop: '0.25rem',
                  borderRadius: '3px',
                  whiteSpace: 'pre-wrap'
                }}>
                  {tcResult.actualOutput || '(no output)'}
                </div>
              </div>

              {tcResult.error && (
                <div>
                  <strong style={{ color: '#dc3545' }}>Error:</strong>
                  <div style={{ 
                    background: '#ffe6e6', 
                    padding: '0.5rem', 
                    marginTop: '0.25rem',
                    borderRadius: '3px',
                    color: '#dc3545',
                    whiteSpace: 'pre-wrap'
                  }}>
                    {tcResult.error}
                  </div>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    );
  };

  return (
    <div style={{ height: '300px', display: 'flex', flexDirection: 'column' }}>
      {/* Tab Headers */}
      <div style={{ 
        display: 'flex',
        borderBottom: '1px solid #e0e0e0',
        marginBottom: '1rem'
      }}>
        <button
          onClick={() => setActiveTab('output')}
          style={{
            padding: '0.5rem 1rem',
            border: 'none',
            background: activeTab === 'output' ? '#667eea' : 'transparent',
            color: activeTab === 'output' ? 'white' : '#666',
            borderRadius: '5px 5px 0 0',
            cursor: 'pointer',
            marginRight: '5px'
          }}
        >
          Output
        </button>
        {result && result.testCaseResults && (
          <button
            onClick={() => setActiveTab('details')}
            style={{
              padding: '0.5rem 1rem',
              border: 'none',
              background: activeTab === 'details' ? '#667eea' : 'transparent',
              color: activeTab === 'details' ? 'white' : '#666',
              borderRadius: '5px 5px 0 0',
              cursor: 'pointer'
            }}
          >
            Test Results
          </button>
        )}
      </div>

      {/* Tab Content */}
      <div style={{ flex: 1, overflow: 'auto' }}>
        {activeTab === 'output' && (
          <div className={`output-panel ${getOutputClass()}`}>
            {isRunning ? (
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <div className="spinner" style={{ width: '20px', height: '20px' }}></div>
                <span>Executing code...</span>
              </div>
            ) : (
              <div>
                {output || 'No output yet. Run your code to see results.'}
                
                {result && result.testCasesPassed !== undefined && (
                  <div style={{ 
                    marginTop: '1rem',
                    padding: '0.75rem',
                    background: result.allTestCasesPassed ? 'rgba(40, 167, 69, 0.1)' : 'rgba(220, 53, 69, 0.1)',
                    borderRadius: '5px',
                    border: `1px solid ${result.allTestCasesPassed ? '#28a745' : '#dc3545'}`
                  }}>
                    <strong>
                      Test Cases: {result.testCasesPassed}/{result.totalTestCases} passed
                    </strong>
                    {result.executionTime && (
                      <div style={{ fontSize: '0.9rem', marginTop: '0.25rem' }}>
                        Execution Time: {result.executionTime}ms
                      </div>
                    )}
                  </div>
                )}
              </div>
            )}
          </div>
        )}

        {activeTab === 'details' && (
          <div style={{ padding: '1rem', background: '#f8f9fa', borderRadius: '5px', height: '100%', overflow: 'auto' }}>
            {renderTestCaseResults()}
          </div>
        )}
      </div>
    </div>
  );
};

export default OutputPanel;