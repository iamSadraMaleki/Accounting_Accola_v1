
import React from 'react';
import { useAuth } from '../context/AuthContext'; 

const HomePage = () => {
  const { user } = useAuth();

  return (
    <div>
      <h2>به سیستم حسابداری خوش آمدید!</h2>
      {user && <p>سلام، {user.username}!</p>}
      <p>برای شروع، از منوی سمت راست گزینه مورد نظر خود را انتخاب کنید.</p>
    </div>
  );
};

export default HomePage;