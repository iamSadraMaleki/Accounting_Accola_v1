
import React, { useState } from 'react'; 
import UserProfileForm from '../components/UserProfileForm.jsx'; 
import ChangePasswordForm from '../components/ChangePasswordForm.jsx'; 
import { useAuth } from '../context/AuthContext.jsx';
import CustomerCode from '../components/CustomerCode.jsx';
import './DashboardPage.css'; 

const DashboardPage = () => {
    const { user } = useAuth();
    
    const [activeTab, setActiveTab] = useState('profile');
    
    return (
        <div className="dashboard-container">
          
            <h1>داشبورد {user ? `- خوش آمدید ${user.username}!` : ''}</h1>

          
            <div className="dashboard-tabs">
                <button
                    className={`tab-button ${activeTab === 'profile' ? 'active' : ''}`}
                    onClick={() => setActiveTab('profile')}
                >
                    اطلاعات هویتی
                </button>
                <button
                    className={`tab-button ${activeTab === 'password' ? 'active' : ''}`}
                    onClick={() => setActiveTab('password')}
                >
                    تغییر رمز عبور
                </button>
                <button
                    className={`tab-button ${activeTab === 'customerCode' ? 'active' : ''}`}
                    onClick={() => setActiveTab('customerCode')}
            
                >
                     کد مشتری
                </button>
            </div>

            
            <div className="dashboard-tab-content">
                {activeTab === 'profile' && (
                    <UserProfileForm /> 
                )}
                {activeTab === 'password' && (
                    <ChangePasswordForm /> 
                )}
                {activeTab === 'customerCode' && <CustomerCode />}
            </div>

        </div>
    );
};

export default DashboardPage;