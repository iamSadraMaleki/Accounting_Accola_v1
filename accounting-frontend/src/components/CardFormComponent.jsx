
import React, { useState } from 'react';
import apiClient from '../api/axiosConfig'; 
import './CardFormComponent.css'; 

const CardFormComponent = ({ onSave, onCancel }) => {
    const [cardData, setCardData] = useState({
        cardNumber: '',
        bankName: '',
        expiryMonth: '',
        expiryYear: '',
        cardHolderName: '',
        initialBalance: '0', 
    });
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState('');
    const [fieldErrors, setFieldErrors] = useState({}); 

    const handleChange = (e) => {
        const { name, value } = e.target;
        setCardData(prev => ({ ...prev, [name]: value }));
        // پاک کردن خطای فیلد
        if (fieldErrors[name]) {
            setFieldErrors(prev => ({ ...prev, [name]: null }));
        }
        setError('');
    };

   
    const validateForm = () => {
        const errors = {};
        if (!cardData.cardNumber || !/^\d{16}$/.test(cardData.cardNumber)) {
            errors.cardNumber = 'شماره کارت باید 16 رقم باشد.';
        }
        if (!cardData.bankName.trim()) {
            errors.bankName = 'نام بانک الزامی است.';
        }
        const month = parseInt(cardData.expiryMonth, 10);
        if (!month || month < 1 || month > 12) {
            errors.expiryMonth = 'ماه انقضا (1-12) نامعتبر است.';
        }
        const year = parseInt(cardData.expiryYear, 10);
       
        if (!year || year < 1400 || year > 1500) {
            errors.expiryYear = 'سال انقضا نامعتبر است.';
        }
       

        if (!cardData.cardHolderName.trim()) {
            errors.cardHolderName = 'نام صاحب کارت الزامی است.';
        }
         if (isNaN(parseFloat(cardData.initialBalance)) || parseFloat(cardData.initialBalance) < 0) {
             errors.initialBalance = 'موجودی اولیه باید یک عدد مثبت یا صفر باشد.';
         }

        setFieldErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        if (!validateForm()) return;

        setIsSaving(true);

       
        const dataToSend = {
            ...cardData,
            expiryMonth: parseInt(cardData.expiryMonth, 10),
            expiryYear: parseInt(cardData.expiryYear, 10),
            initialBalance: parseFloat(cardData.initialBalance)
        };

        try {
           
            await apiClient.post('/api/cards', dataToSend);
            setIsSaving(false);
            
            onSave();

        } catch (err) {
            console.error("Error adding card:", err);
            setIsSaving(false);
            if (err.response && err.response.data) {
                 
                 if (typeof err.response.data === 'object' && err.response.data.error) {
                     setError(err.response.data.error);
                 } else if (typeof err.response.data === 'string') {
                    setError(err.response.data);
                 }
                 else {
                      setError('خطا در افزودن کارت.');
                 }

            } else {
                setError('خطا در ارتباط با سرور هنگام افزودن کارت.');
            }
        }
    };

    return (
        
            <div className="card-form-container">
                <form onSubmit={handleSubmit} className="card-form">
                    <h4>افزودن کارت بانکی جدید</h4>
                    {error && <p className="error-message">{error}</p>}

                    <div className="form-group">
                        <label htmlFor="cardNumber">شماره کارت (۱۶ رقم):</label>
                        <input type="text" id="cardNumber" name="cardNumber" value={cardData.cardNumber} onChange={handleChange} maxLength="16" className={fieldErrors.cardNumber ? 'input-error' : ''} required />
                        {fieldErrors.cardNumber && <span className="field-error">{fieldErrors.cardNumber}</span>}
                    </div>
                    <div className="form-group">
                        <label htmlFor="bankName">نام بانک:</label>
                        <input type="text" id="bankName" name="bankName" value={cardData.bankName} onChange={handleChange} className={fieldErrors.bankName ? 'input-error' : ''} required />
                        {fieldErrors.bankName && <span className="field-error">{fieldErrors.bankName}</span>}
                    </div>
                   
                    <div className="form-row"> 
                        <div className="form-group">
                            <label htmlFor="expiryMonth">ماه انقضا (MM):</label>
                            <input type="number" id="expiryMonth" name="expiryMonth" value={cardData.expiryMonth} onChange={handleChange} min="1" max="12" placeholder="مثلا 05" className={fieldErrors.expiryMonth ? 'input-error' : ''} required/>
                             {fieldErrors.expiryMonth && <span className="field-error">{fieldErrors.expiryMonth}</span>}
                        </div>
                         <div className="form-group">
                            <label htmlFor="expiryYear">سال انقضا (YYYY):</label>
                            <input type="number" id="expiryYear" name="expiryYear" value={cardData.expiryYear} onChange={handleChange} placeholder="مثلا 1405" className={fieldErrors.expiryYear ? 'input-error' : ''} required/>
                             {fieldErrors.expiryYear && <span className="field-error">{fieldErrors.expiryYear}</span>}
                        </div>
                    </div>
                     <div className="form-group">
                        <label htmlFor="cardHolderName">نام صاحب کارت:</label>
                        <input type="text" id="cardHolderName" name="cardHolderName" value={cardData.cardHolderName} onChange={handleChange} className={fieldErrors.cardHolderName ? 'input-error' : ''} required/>
                         {fieldErrors.cardHolderName && <span className="field-error">{fieldErrors.cardHolderName}</span>}
                    </div>
                    <div className="form-group">
                        <label htmlFor="initialBalance">موجودی اولیه (ریال):</label>
                        <input type="number" id="initialBalance" name="initialBalance" value={cardData.initialBalance} onChange={handleChange} step="any" min="0" className={fieldErrors.initialBalance ? 'input-error' : ''} required/>
                        {fieldErrors.initialBalance && <span className="field-error">{fieldErrors.initialBalance}</span>}
                    </div>

                    
                    <div className="card-form-actions">
                        <button type="submit" disabled={isSaving} className="save-btn">
                            {isSaving ? 'در حال ذخیره...' : 'ذخیره کارت'}
                        </button>
                        
                        <button type="button" onClick={onCancel} className="cancel-btn">لغو</button>
                    </div>
                </form>
            </div>
    );
};

export default CardFormComponent;