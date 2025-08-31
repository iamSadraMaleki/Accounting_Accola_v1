
import React, { useState, useEffect } from 'react';
import apiClient from '../api/axiosConfig'; 
import { useAuth } from '../context/AuthContext.jsx';
import './CustomerCode.css'; 

const CustomerCode = () => {
    const { user: authUser } = useAuth();
    const [customerCode, setCustomerCode] = useState(null);
    const [username, setUsername] = useState(authUser?.username || '');
    const [isLoading, setIsLoading] = useState(true);
    const [generateLoading, setGenerateLoading] = useState(false);
    const [error, setError] = useState('');

   
    const fetchInitialData = async () => {
        setIsLoading(true);
        setError('');
        try {
            console.log("[CustomerCode] Fetching profile data...");
            const response = await apiClient.get('/api/profile/me');
            console.log("[CustomerCode] Profile data received:", response.data); 
            console.log("[CustomerCode] Received raw customerCode from API:", response.data?.customerCode);

            if (response.data) {
                const fetchedCode = response.data.customerCode || null;
                setCustomerCode(fetchedCode);
                setUsername(response.data.username);
                console.log("[CustomerCode] Set customerCode state to:", fetchedCode); 
            } else {
                 setCustomerCode(null);
                 console.log("[CustomerCode] No profile data received, set customerCode state to null");
            }
        } catch (err) {
            console.error("[CustomerCode] Error fetching customer code:", err);
            setError("خطا در دریافت اطلاعات کد مشتری.");
        } finally {
            setIsLoading(false);
        }
    };

    
    useEffect(() => {
        fetchInitialData();
    }, []);

     
    const handleGenerateCode = async () => {
        setGenerateLoading(true);
        setError('');
        try {
            console.log("[CustomerCode] Attempting to generate customer code...");
            const response = await apiClient.post('/api/profile/me/customer-code');
            console.log("[CustomerCode] Code generated/returned:", response.data);
            setCustomerCode(response.data); 
        } catch (err) {
            console.error("[CustomerCode] Error generating customer code:", err);
             if (err.response && (err.response.status === 403 || err.response.status === 400)) {
                 setError(err.response.data || "امکان تولید مجدد کد وجود ندارد.");
                 
                 fetchInitialData();
             } else {
                setError("خطا در تولید کد مشتری.");
             }
        } finally {
            setGenerateLoading(false);
        }
    };

    
    const hasCustomerCode = customerCode && String(customerCode).trim() !== ''; 

   
    console.log("[CustomerCode] Rendering. State customerCode:", customerCode, "| Has Code:", hasCustomerCode);

    if (isLoading) {
        return <p>در حال بارگذاری اطلاعات کد مشتری...</p>;
    }

    return (
        <div className="customer-code-container">
            <h3>کد مشتری</h3>

            {error && <p className="error-message">{error}</p>}

            <div className="info-section">
                <p>
                    <strong>نام کاربری:</strong> {username || 'نامشخص'}
                </p>

                
                {hasCustomerCode && (
                    <>
                        <p>
                            <strong>کد مشتری:</strong>
                            <span className="customer-code-value">{customerCode}</span>
                        </p>
                        <p>
                            <strong>وضعیت:</strong> <span className="status-active">فعال</span>
                        </p>
                    </>
                )}
            </div>

          
            {!hasCustomerCode && (
                <div className="generate-section">
                    <p>شما هنوز کد مشتری دریافت نکرده‌اید.</p>
                    <button
                        onClick={handleGenerateCode}
                        disabled={generateLoading}
                        className="generate-button"
                    >
                        {generateLoading ? 'در حال تولید...' : 'تولید کد مشتری'}
                    </button>
                </div>
            )}
        </div>
    );
};

export default CustomerCode;