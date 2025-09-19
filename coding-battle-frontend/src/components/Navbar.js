import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Navbar = () => {
  const location = useLocation();

  const isActive = (path) => {
    return location.pathname === path ? 'nav-link active' : 'nav-link';
  };

  return (
    <nav className="navbar">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', width: '100%' }}>
        <Link to="/" className="navbar-brand">
          🚀 Coding Battle
        </Link>
        
        <ul className="navbar-nav">
          <li>
            <Link to="/" className={isActive('/')}>
              Home
            </Link>
          </li>
          <li>
            <Link to="/create-room" className={isActive('/create-room')}>
              Create Room
            </Link>
          </li>
          <li>
            <Link to="/join-room" className={isActive('/join-room')}>
              Join Room
            </Link>
          </li>
        </ul>
      </div>
    </nav>
  );
};

export default Navbar;