import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
  return (
    <div className="container">
      <div className="card fade-in">
        <h1 style={{ 
          fontSize: '3rem', 
          fontWeight: 'bold', 
          marginBottom: '2rem',
          background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
          WebkitBackgroundClip: 'text',
          WebkitTextFillColor: 'transparent',
          backgroundClip: 'text'
        }}>
          Welcome to Coding Battle
        </h1>
        
        <p style={{ 
          fontSize: '1.2rem', 
          color: '#666', 
          marginBottom: '3rem',
          lineHeight: '1.6'
        }}>
          Challenge yourself and others in real-time coding competitions. 
          Create rooms with custom problems or join existing battles to test your skills!
        </p>

        <div className="grid grid-2" style={{ maxWidth: '600px', margin: '0 auto' }}>
          <div style={{ textAlign: 'center', padding: '2rem' }}>
            <div style={{ 
              fontSize: '4rem', 
              marginBottom: '1rem',
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text'
            }}>
              🏗️
            </div>
            <h3 style={{ marginBottom: '1rem', color: '#333' }}>Create Room</h3>
            <p style={{ color: '#666', marginBottom: '2rem' }}>
              Set up your own coding challenge with custom questions and test cases.
            </p>
            <Link to="/create-room" className="btn btn-primary">
              Create Room
            </Link>
          </div>

          <div style={{ textAlign: 'center', padding: '2rem' }}>
            <div style={{ 
              fontSize: '4rem', 
              marginBottom: '1rem',
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text'
            }}>
              🚪
            </div>
            <h3 style={{ marginBottom: '1rem', color: '#333' }}>Join Room</h3>
            <p style={{ color: '#666', marginBottom: '2rem' }}>
              Enter a room ID to join an existing coding battle and compete with others.
            </p>
            <Link to="/join-room" className="btn btn-secondary">
              Join Room
            </Link>
          </div>
        </div>

        <div style={{ 
          marginTop: '4rem', 
          padding: '2rem', 
          background: 'rgba(102, 126, 234, 0.1)', 
          borderRadius: '10px' 
        }}>
          <h3 style={{ marginBottom: '1rem', color: '#333' }}>Features</h3>
          <div className="grid grid-3" style={{ textAlign: 'left' }}>
            <div>
              <h4 style={{ color: '#667eea' }}>🌐 Multiple Languages</h4>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>
                Support for Java, Python, C++, C, and JavaScript
              </p>
            </div>
            <div>
              <h4 style={{ color: '#667eea' }}>⚡ Real-time Execution</h4>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>
                Run and test your code instantly with custom test cases
              </p>
            </div>
            <div>
              <h4 style={{ color: '#667eea' }}>🏆 Competition Mode</h4>
              <p style={{ color: '#666', fontSize: '0.9rem' }}>
                Submit solutions and see who solves problems first
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;