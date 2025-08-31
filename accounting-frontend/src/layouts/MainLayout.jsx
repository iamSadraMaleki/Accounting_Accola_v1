import React from 'react';
import Header from './Header.jsx'; 
import Footer from './Footer.jsx'; 
import Sidebar from './Sidebar.jsx'; 
import './MainLayout.css'; 

const MainLayout = ({ children }) => {
  return (
    
    <div className="main-layout">
      <Header />
      <div className="main-area">
       
        <main className="main-content">
          {children}
        </main>
        
        <Sidebar />
      </div>
      <Footer />
    </div>
  );
};

export default MainLayout;