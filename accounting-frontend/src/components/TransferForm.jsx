
import React, { useState, useEffect } from 'react';

import apiClient from '../api/axiosConfig'; 

import { formatCurrency } from '../utils/formatters'; 
import './TransferForm.css'; 

const TransferForm = ({ onSuccess }) => {
    const [formData, setFormData] = useState({
        sourceCardNumber: '',      
        destinationCardNumber: '', 
        amount: '',
        description: ''
    });
    
    const [isLookingUp, setIsLookingUp] = useState(false);
    const [destHolderName, setDestHolderName] = useState('');
    const [lookupError, setLookupError] = useState('');
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});

    
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setError('');
        setSuccessMessage('');
        if (name === 'destinationCardNumber') {
            setDestHolderName('');
            setLookupError('');
        }
        if (fieldErrors[name]) {
            setFieldErrors(prev => ({ ...prev, [name]: null }));
        }
    };

   
    const handleDestinationLookup = async () => {
        const destCardNum = formData.destinationCardNumber;
        if (destCardNum && /^\d{16}$/.test(destCardNum)) {
            setIsLookingUp(true); setLookupError(''); setDestHolderName('');
            try {
                const response = await apiClient.get(`/api/transfers/lookup/${destCardNum}`);
                if (response.data && response.data.cardHolderName) {
                    setDestHolderName(response.data.cardHolderName);
                } else { setLookupError("کارت مقصد در سیستم یافت نشد."); }
            } catch (err) {
                 if (err.response && err.response.status === 404) { setLookupError("کارتی با این شماره یافت نشد."); }
                 else { setLookupError("خطا در جستجوی کارت مقصد."); }
            } finally { setIsLookingUp(false); }
        } else { setLookupError(destCardNum ? "شماره کارت مقصد باید ۱۶ رقم باشد." : ''); }
    };

    
    const validateForm = () => {
        const errors = {};
       
        if (!formData.sourceCardNumber || !/^\d{16}$/.test(formData.sourceCardNumber)) {
            errors.sourceCardNumber = 'شماره کارت مبدأ باید ۱۶ رقم عددی باشد.';
        }
        if (!formData.destinationCardNumber || !/^\d{16}$/.test(formData.destinationCardNumber)) {
            errors.destinationCardNumber = 'شماره کارت مقصد باید ۱۶ رقم عددی باشد.';
        }
        // ------------------------------------
        if (formData.sourceCardNumber === formData.destinationCardNumber) {
            
             errors.sourceCardNumber = "کارت مبدأ و مقصد نمی‌تواند یکسان باشد.";
             errors.destinationCardNumber = "کارت مبدأ و مقصد نمی‌تواند یکسان باشد.";
        }
        const amountNumber = parseFloat(formData.amount);
        if (isNaN(amountNumber) || amountNumber <= 0) { errors.amount = 'مبلغ انتقال باید یک عدد مثبت باشد.'; }

        setFieldErrors(errors);
        return Object.keys(errors).length === 0;
    };

  
    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');
        if (!validateForm()) {
            setError("لطفا خطاهای فرم را برطرف کنید.");
            return;
        }
        setIsSaving(true);

        const amountNumber = parseFloat(formData.amount);
        const requestData = {
            sourceCardNumber: formData.sourceCardNumber, 
            destinationCardNumber: formData.destinationCardNumber,
            amount: amountNumber,
            description: formData.description
        };

        console.log("[TransferForm] Sending Transfer Data:", JSON.stringify(requestData, null, 2));

        try {
            await apiClient.post('/api/transfers', requestData);
            setSuccessMessage('انتقال وجه با موفقیت ثبت شد!');
            setFormData({ sourceCardNumber: '', destinationCardNumber: '', amount: '', description: '' });
            setDestHolderName('');
            setLookupError('');
            setFieldErrors({});
            if (onSuccess) onSuccess();
        } catch (err) {
            console.error("[TransferForm] Error recording transfer:", err);
            setIsSaving(false);
            // ... مدیریت خطای قبلی ...
             if (err.response && err.response.data) {
                 console.error("[TransferForm] Backend Error:", JSON.stringify(err.response.data, null, 2));
                 if (typeof err.response.data === 'object' && err.response.data.error) { setError(err.response.data.error); }
                 else if (typeof err.response.data === 'string') { setError(err.response.data); }
                 else { setError('خطا در ثبت انتقال وجه.'); }
             } else if (err.request) { setError('خطا در ارتباط با سرور.'); }
             else { setError('یک خطای پیش‌بینی نشده رخ داد.'); }
        }
    };

   

    return (
        <form onSubmit={handleSubmit} className="transfer-form">
            {error && <p className="error-message">{error}</p>}
            {successMessage && <p className="success-message">{successMessage}</p>}

           
            <div className="form-group">
                <label htmlFor="sourceCardNumber">از کارت (۱۶ رقم):</label>
                <input
                    type="text"
                    inputMode="numeric"
                    pattern="\d{16}"
                    maxLength="16"
                    id="sourceCardNumber"
                    name="sourceCardNumber"
                    value={formData.sourceCardNumber}
                    onChange={handleChange}
                    required
                    className={fieldErrors.sourceCardNumber ? 'input-error' : ''}
                    placeholder="شماره ۱۶ رقمی کارت مبدأ"
                />
                 {fieldErrors.sourceCardNumber && <span className="field-error">{fieldErrors.sourceCardNumber}</span>}
            </div>
            {/* --------------------------------- */}

            <div className="form-group">
                <label htmlFor="destinationCardNumber">به کارت:</label>
                <input
                    type="text" inputMode="numeric" pattern="\d{16}" maxLength="16"
                    id="destinationCardNumber" name="destinationCardNumber"
                    value={formData.destinationCardNumber}
                    onChange={handleChange}
                    onBlur={handleDestinationLookup}
                    required
                    placeholder="شماره ۱۶ رقمی کارت مقصد"
                    className={fieldErrors.destinationCardNumber ? 'input-error' : ''}
                />
                 {fieldErrors.destinationCardNumber && <span className="field-error">{fieldErrors.destinationCardNumber}</span>}
                <div className="lookup-status">
                    {isLookingUp && <small>در حال بررسی...</small>}
                    {lookupError && <small className="error-text">{lookupError}</small>}
                    {destHolderName && <small className="success-text">صاحب کارت: {destHolderName}</small>}
                 </div>
            </div>

            <div className="form-group">
                <label htmlFor="amount">مبلغ انتقال (ریال):</label>
                <input type="number" id="amount" name="amount" value={formData.amount} onChange={handleChange} required min="1" step="any" className={fieldErrors.amount ? 'input-error' : ''}/>
                 {fieldErrors.amount && <span className="field-error">{fieldErrors.amount}</span>}
            </div>

            <div className="form-group">
                <label htmlFor="description">توضیحات (اختیاری):</label>
                <input type="text" id="description" name="description" value={formData.description} onChange={handleChange} />
            </div>

            
            <button type="submit" disabled={isSaving} className="submit-btn">
                {isSaving ? 'در حال انتقال...' : 'ثبت انتقال وجه'}
            </button>

        </form>
    );
};

export default TransferForm;