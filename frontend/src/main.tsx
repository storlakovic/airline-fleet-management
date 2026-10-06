import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import HomeView from './views/HomeView.tsx';
import './styles/main.css';

const root = document.getElementById('app');
if (!root) throw new Error('App root element is missing.');

createRoot(root).render(
  <StrictMode>
    <HomeView />
  </StrictMode>,
);
