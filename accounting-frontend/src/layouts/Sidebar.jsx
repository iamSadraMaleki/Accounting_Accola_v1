
import React from 'react';

import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext.jsx';
import './Sidebar.css';

const Sidebar = () => {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN'; 
  const isBusiness = user?.role === 'BUSINESS';

  return (
    <aside className="app-sidebar">

      
      {!isAdmin && (
       
        <>
          <h4>منوی اصلی</h4>
        
          <NavLink to="/" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"} end>صفحه اصلی</NavLink>
          <NavLink to="/dashboard" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>داشبورد</NavLink>
          <NavLink to="/cards" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>کارت‌های بانکی</NavLink>
          <NavLink to="/payments" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>دریافت و پرداخت</NavLink>
          <NavLink to="/reports" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>گزارشات</NavLink>
          <NavLink to="/requests" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>درخواست‌ها</NavLink>
          <NavLink to="/invoices" className="sidebar-link">مدیریت فاکتور</NavLink> 
        </>
       
      )}
    
      {isAdmin && (
        <>
          <hr className="sidebar-divider" />
          <h4>پنل ادمین</h4>
          
          <NavLink to="/admin/requests" className={({ isActive }) => isActive ? "sidebar-link active" : "sidebar-link"}>مدیریت درخواست‌ها</NavLink>
          
        </>
      )}
      

    </aside>
  );
};

export default Sidebar;