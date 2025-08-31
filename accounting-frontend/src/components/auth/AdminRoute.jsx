
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext'; 

const AdminRoute = ({ children }) => {
  const { isLoggedIn, user, isLoading } = useAuth();

  
  if (isLoading) {
    return <div>در حال بررسی دسترسی...</div>; 
  }

 
  if (!isLoggedIn) {
    console.log("[AdminRoute] User not logged in. Redirecting to /login.");
    return <Navigate to="/login" replace />;
  }

  
  if (user?.role !== 'ADMIN') {
    console.warn(`[AdminRoute] User ${user?.username} with role ${user?.role} attempted to access admin route. Redirecting.`);
    
   
    return <Navigate to="/forbidden" replace />; 
  }

 
  console.log("[AdminRoute] Admin access granted.");
  return children;
};


export const ForbiddenPage = () => (
    <div style={{ padding: '50px', textAlign: 'center' }}>
        <h2>دسترسی غیرمجاز (403 Forbidden)</h2>
        <p>شما اجازه دسترسی به این صفحه را ندارید.</p>
        <Link to="/">بازگشت به صفحه اصلی</Link> 
    </div>
);


export default AdminRoute;