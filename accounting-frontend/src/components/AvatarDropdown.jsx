
import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Clock from './Clock.jsx';
import { useAuth } from '../context/AuthContext.jsx'; // <<< ایمپورت هوک useAuth
import './AvatarDropdown.css';


const AvatarDropdown = () => {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();

  
  const { user, isLoggedIn, logout } = useAuth();

  const toggleDropdown = () => setIsOpen(!isOpen);

  const handleLogout = () => {
    console.log("Logout button clicked. Calling logout from context...");
    logout();
    setIsOpen(false);
    navigate('/login'); 
  };

  
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [dropdownRef]);

  
  if (!isLoggedIn || !user) {
      
      return null;
  }

 
  const avatarInitial = user.username ? user.username.charAt(0).toUpperCase() : '?';

  return (
    <div className="avatar-dropdown-container" ref={dropdownRef}>
     
      <div className="avatar-circle" onClick={toggleDropdown}>
        {avatarInitial}
      </div>

      
      {isOpen && (
        <div className="dropdown-menu">
         
          <div className="dropdown-item user-info">
            <div>
                <span>نام کاربری:</span>
                <span style={{ fontWeight: 'normal', marginRight: '5px' }}>{user.username}</span>
            </div>
            <div>
                <span>نقش:</span>
                <span style={{ fontWeight: 'normal', marginRight: '5px' }}>{user.role || 'کاربر'}</span>
            </div>
            <div>
                <span>وضعیت:</span>
                <span style={{ fontWeight: 'normal', marginRight: '5px' }}>
                    <span className="online-status" style={{ backgroundColor: 'lightgreen' }}></span>
                    آنلاین
                </span>
            </div>
          </div>

         
          <div className="dropdown-item clock-item"> <Clock /> </div>


          
          <button onClick={handleLogout} className="dropdown-item logout-button"> خروج </button>

        </div>
      )}
    </div>
  );
};

export default AvatarDropdown;