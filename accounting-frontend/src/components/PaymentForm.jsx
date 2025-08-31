
import React, { useState } from 'react'; 
import apiClient from '../api/axiosConfig';

import './PaymentForm.css';
import { Link } from 'react-router-dom'; 

const PaymentForm = ({ onSuccess }) => {
    const [formData, setFormData] = useState({
        shopName: '', transactionTime: '', terminalInfo: '',
        cardNumber: '', trackingNumber: '', amount: ''
    });
    
    // --------------------------------------
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});

    
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setError('');
        setSuccessMessage('');
        if (fieldErrors[name]) {
            setFieldErrors(prev => ({ ...prev, [name]: null }));
        }
    };

     
     const validateForm = () => {
        const errors = {};
       
        if (!formData.cardNumber || !/^\d{16}$/.test(formData.cardNumber)) {
            errors.cardNumber = 'شماره کارت باید ۱۶ رقم عددی باشد.';
        }
       
        if (!formData.shopName.trim()) { errors.shopName = 'نام فروشگاه الزامی است.'; }
        if (!formData.trackingNumber.trim()) { errors.trackingNumber = 'شماره پیگیری الزامی است.'; }
        const amountNumber = parseFloat(formData.amount);
        if (isNaN(amountNumber) || amountNumber <= 0) { errors.amount = 'مبلغ پرداخت باید یک عدد مثبت باشد.'; }

        setFieldErrors(errors);
        return Object.keys(errors).length === 0;
    };


   
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');
        if (!validateForm()) return;
        setIsSaving(true);

        const amountNumber = parseFloat(formData.amount);

        const requestData = {
            shopName: formData.shopName,
            transactionTime: formData.transactionTime || null,
            terminalInfo: formData.terminalInfo,
            cardNumber: formData.cardNumber, 
            trackingNumber: formData.trackingNumber,
            amount: amountNumber
        };

        console.log("[PaymentForm] Sending Payment Data to Backend:", JSON.stringify(requestData, null, 2));

        try {
            await apiClient.post('/api/payments', requestData);
            setSuccessMessage('پرداخت با موفقیت ثبت شد!');
            setFormData({ 
                shopName: '', transactionTime: '', terminalInfo: '',
                cardNumber: '', trackingNumber: '', amount: ''
            });
            setFieldErrors({});
            if (onSuccess) onSuccess();

        } catch (err) {
           
             console.error("[PaymentForm] Error recording payment:", err);
            setIsSaving(false);
             if (err.response && err.response.data) {
                 console.error("[PaymentForm] Backend Error Response Data:", JSON.stringify(err.response.data, null, 2));
                 
                  if (typeof err.response.data === 'object' && err.response.data.error) { setError(err.response.data.error); }
                  else if (typeof err.response.data === 'string') { setError(err.response.data); }
                  else if (typeof err.response.data === 'object' && err.response.status === 400) { setError("خطا در اطلاعات وارد شده."); }
                  else { setError('خطا در ثبت پرداخت.'); }

             } else if (err.request) { setError('خطا در ارتباط با سرور.'); }
             else { setError('یک خطای پیش‌بینی نشده رخ داد.'); }
        }
        
    };

   

    return (
        <form onSubmit={handleSubmit} className="payment-form">
            {error && <p className="error-message">{error}</p>}
            {successMessage && <p className="success-message">{successMessage}</p>}

            
            <div className="form-group">
                <label htmlFor="cardNumber">شماره کارت پرداخت کننده (۱۶ رقم):</label>
                <input
                    type="text" 
                    inputMode="numeric" 
                    pattern="\d{16}" 
                    maxLength="16"   
                    id="cardNumber"
                    name="cardNumber"
                    value={formData.cardNumber}
                    onChange={handleChange}
                    required
                    className={fieldErrors.cardNumber ? 'input-error' : ''}
                    placeholder="•••• •••• •••• ••••"
                />
                {fieldErrors.cardNumber && <span className="field-error">{fieldErrors.cardNumber}</span>}
            </div>
            

           
            <div className="form-group"> <label htmlFor="amount">مبلغ پرداخت (ریال):</label> <input type="number" id="amount" name="amount" value={formData.amount} onChange={handleChange} required min="1" step="any" className={fieldErrors.amount ? 'input-error' : ''}/> {fieldErrors.amount && <span className="field-error">{fieldErrors.amount}</span>} </div>
            <div className="form-group"> <label htmlFor="shopName">نام فروشگاه / گیرنده:</label> <input type="text" id="shopName" name="shopName" value={formData.shopName} onChange={handleChange} required className={fieldErrors.shopName ? 'input-error' : ''}/> {fieldErrors.shopName && <span className="field-error">{fieldErrors.shopName}</span>} </div>
            <div className="form-group"> <label htmlFor="trackingNumber">شماره پیگیری / مرجع:</label> <input type="text" id="trackingNumber" name="trackingNumber" value={formData.trackingNumber} onChange={handleChange} required className={fieldErrors.trackingNumber ? 'input-error' : ''}/> {fieldErrors.trackingNumber && <span className="field-error">{fieldErrors.trackingNumber}</span>} </div>
            <div className="form-group"> <label htmlFor="transactionTime">زمان تراکنش (اختیاری):</label> <input type="datetime-local" id="transactionTime" name="transactionTime" value={formData.transactionTime} onChange={handleChange} /> <small>اگر خالی بگذارید، زمان فعلی ثبت می‌شود.</small> </div>
            <div className="form-group"> <label htmlFor="terminalInfo">اطلاعات پایانه (اختیاری):</label> <input type="text" id="terminalInfo" name="terminalInfo" value={formData.terminalInfo} onChange={handleChange} /> </div>

           
            <button type="submit" disabled={isSaving } className="submit-btn">
                {isSaving ? 'در حال ثبت...' : 'ثبت پرداخت'}
            </button>

        </form>
    );
};

export default PaymentForm;