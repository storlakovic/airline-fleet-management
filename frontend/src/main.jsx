import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import HomeView from './views/HomeView.jsx';
import './styles/main.css';

createRoot(document.getElementById('app')).render(
  <StrictMode>
    <HomeView />
  </StrictMode>,
);
