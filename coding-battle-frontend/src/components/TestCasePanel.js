import React, { useState } from 'react';

const TestCasePanel = ({ testCases = [] }) => {
  const [expandedTestCase, setExpandedTestCase] = useState(null);

  // Filter out hidden test cases for display
  const visibleTestCases = testCases.filter(tc => !tc.isHidden);

  const toggleTestCase = (index) => {
    setExpandedTestCase(expandedTestCase === index ? null : index);
  };

  if (!visibleTestCases.length) {
    return (
      <div style={{ padding: '1rem', textAlign: 'center', color: '#666' }}>
        No test cases available
      </div>
    );
  }

  return (
    <div>
      <h4 style={{ marginBottom: '1rem', color: '#333' }}>
        Sample Test Cases ({visibleTestCases.length})
      </h4>
      
      {visibleTestCases.map((testCase, index) => (
        <div key={index} className="test-case" style={{ marginBottom: '1rem' }}>
          <div 
            className="test-case-header"
            style={{ 
              cursor: 'pointer',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              padding: '0.5rem',
              background: '#f8f9fa',
              borderRadius: '5px',
              border: '1px solid #e9ecef'
            }}
            onClick={() => toggleTestCase(index)}
          >
            <span>Test Case {index + 1}</span>
            <span style={{ 
              fontSize: '0.8rem',
              color: '#666',
              transform: expandedTestCase === index ? 'rotate(180deg)' : 'rotate(0deg)',
              transition: 'transform 0.2s'
            }}>
              ▼
            </span>
          </div>
          
          {testCase.description && (
            <div style={{ 
              padding: '0.5rem',
              fontStyle: 'italic',
              color: '#666',
              fontSize: '0.9rem'
            }}>
              {testCase.description}
            </div>
          )}
          
          {expandedTestCase === index && (
            <div style={{ padding: '1rem', background: '#f8f9fa' }}>
              <div style={{ marginBottom: '1rem' }}>
                <div style={{ 
                  fontWeight: '600',
                  marginBottom: '0.5rem',
                  color: '#495057',
                  fontSize: '0.9rem'
                }}>
                  INPUT:
                </div>
                <div className="test-case-content" style={{
                  fontFamily: '"Courier New", Consolas, monospace',
                  background: 'white',
                  padding: '0.75rem',
                  borderRadius: '4px',
                  border: '1px solid #dee2e6',
                  whiteSpace: 'pre-wrap',
                  fontSize: '0.85rem',
                  color: '#212529',
                  minHeight: '2rem'
                }}>
                  {testCase.input || '(empty)'}
                </div>
              </div>
              
              <div>
                <div style={{ 
                  fontWeight: '600',
                  marginBottom: '0.5rem',
                  color: '#495057',
                  fontSize: '0.9rem'
                }}>
                  EXPECTED OUTPUT:
                </div>
                <div className="test-case-content" style={{
                  fontFamily: '"Courier New", Consolas, monospace',
                  background: 'white',
                  padding: '0.75rem',
                  borderRadius: '4px',
                  border: '1px solid #dee2e6',
                  whiteSpace: 'pre-wrap',
                  fontSize: '0.85rem',
                  color: '#212529',
                  minHeight: '2rem'
                }}>
                  {testCase.expectedOutput || '(empty)'}
                </div>
              </div>
            </div>
          )}
        </div>
      ))}
      
      {testCases.length > visibleTestCases.length && (
        <div style={{ 
          padding: '0.5rem',
          background: '#fff3cd',
          border: '1px solid #ffeaa7',
          borderRadius: '4px',
          fontSize: '0.85rem',
          color: '#856404'
        }}>
          ℹ️ Additional hidden test cases will be used during submission evaluation.
        </div>
      )}
    </div>
  );
};

export default TestCasePanel;