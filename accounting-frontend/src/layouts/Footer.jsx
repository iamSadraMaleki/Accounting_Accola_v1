import React from 'react';
import './Footer.css'; 

const Footer = () => {
  const currentYear = new Date().getFullYear();
  

  return (
    
    <footer className="app-footer">
      حق کپی رایت &copy; {currentYear} - سیستم حسابداری من
    </footer>
  );
};

export default Footer;