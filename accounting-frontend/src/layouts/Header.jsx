
import React from 'react';
import AvatarDropdown from '../components/AvatarDropdown.jsx'; 
import './Header.css';

const Header = () => {
  
  return (
    <header className="app-header">
      
       <div className="header-left">
          <AvatarDropdown />
       </div>

       
       <div className="header-right">
          <div className="app-title">سیستم حسابداری من</div>
       </div>
    </header>
  );
};

export default Header;