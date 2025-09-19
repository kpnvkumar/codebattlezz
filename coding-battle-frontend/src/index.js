import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';

const root = ReactDOM.createRoot(document.getElementById('root'));
window.addEventListener('error', e => {
  if (e.message === 'ResizeObserver loop completed with undelivered notifications.') {
    e.preventDefault();
    e.stopPropagation();
    e.stopImmediatePropagation();
  }
});
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);