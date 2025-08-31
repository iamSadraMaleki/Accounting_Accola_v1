import React from 'react';
import { Link } from 'react-router-dom'; 

const UserMenu = ({ userName = "کاربر مهمان", isLoggedIn = false }) => {
  const handleLogout = () => {
    
    console.log("Logging out...");
    
  };

  const onlineStatusStyle = {
    display: 'inline-block',
    width: '10px',
    height: '10px',
    borderRadius: '50%',
    backgroundColor: isLoggedIn ? 'lightgreen' : 'lightcoral',
    marginLeft: '8px',
    verticalAlign: 'middle',
  };

  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
      <span>
        {userName}
        <span style={onlineStatusStyle} title={isLoggedIn ? "آنلاین" : "آفلاین"}></span>
      </span>
      {isLoggedIn ? (
        <>
          <Link to="/dashboard" style={{ textDecoration: 'none', color: 'inherit' }}>داشبورد</Link>
          <button onClick={handleLogout} style={{ cursor: 'pointer' }}>خروج</button>
        </>
      ) : (
        <Link to="/login" style={{ textDecoration: 'none', color: 'inherit' }}>ورود</Link>
      )}
    </div>
  );
};

export default UserMenu;