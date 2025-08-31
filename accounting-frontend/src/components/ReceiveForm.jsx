
import React, { useState  } from 'react'; 
import apiClient from '../../src/api/axiosConfig';

import './ReceiveForm.css';

const ReceiveForm = ({ onSuccess }) => {
    const [formData, setFormData] = useState({
        destinationCardNumber: '', // << ورودی دستی
        amount: '',
        description: '',
        receiveTime: ''
    });
    
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
       
        if (!formData.destinationCardNumber || !/^\d{16}$/.test(formData.destinationCardNumber)) {
            errors.destinationCardNumber = 'شماره کارت مقصد باید ۱۶ رقم عددی باشد.';
        }
       
        const amountNumber = parseFloat(formData.amount);
        if (isNaN(amountNumber) || amountNumber <= 0) { errors.amount = 'مبلغ دریافتی باید یک عدد مثبت باشد.'; }
        if (!formData.description.trim()) { errors.description = 'توضیحات الزامی است.'; }

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
            destinationCardNumber: formData.destinationCardNumber, 
            amount: amountNumber,
            description: formData.description,
            receiveTime: formData.receiveTime || null
        };

        console.log("[ReceiveForm] Sending Receive Data:", JSON.stringify(requestData, null, 2));

        try {
            await apiClient.post('/api/receives', requestData);
            setSuccessMessage('دریافت وجه با موفقیت ثبت شد!');
            setFormData({ destinationCardNumber: '', amount: '', description: '', receiveTime: '' });
            setFieldErrors({});
            if (onSuccess) onSuccess();
        } catch (err) {
           
            console.error("[ReceiveForm] Error recording receive:", err);
            setIsSaving(false);
             if (err.response && err.response.data) {  }
             else if (err.request) { setError('خطا در ارتباط با سرور.'); }
             else { setError('یک خطای پیش‌بینی نشده رخ داد.'); }
        }
    };

   

    return (
         <form onSubmit={handleSubmit} className="receive-form">
            {error && <p className="error-message">{error}</p>}
            {successMessage && <p className="success-message">{successMessage}</p>}

            
             <div className="form-group">
                <label htmlFor="destinationCardNumber">به کارت (۱۶ رقم):</label>
                <input
                    type="text"
                    inputMode="numeric"
                    pattern="\d{16}"
                    maxLength="16"
                    id="destinationCardNumber"
                    name="destinationCardNumber"
                    value={formData.destinationCardNumber}
                    onChange={handleChange}
                    required
                    className={fieldErrors.destinationCardNumber ? 'input-error' : ''}
                    placeholder="شماره ۱۶ رقمی کارت مقصد"
                />
                 {fieldErrors.destinationCardNumber && <span className="field-error">{fieldErrors.destinationCardNumber}</span>}
            </div>
             {/* ---------------------------------- */}

             <div className="form-group">
                <label htmlFor="amount">مبلغ دریافتی (ریال):</label>
                <input type="number" id="amount" name="amount" value={formData.amount} onChange={handleChange} required min="1" step="any" className={fieldErrors.amount ? 'input-error' : ''}/>
                {fieldErrors.amount && <span className="field-error">{fieldErrors.amount}</span>}
            </div>

            <div className="form-group">
                <label htmlFor="description">توضیحات / منبع:</label>
                <input type="text" id="description" name="description" value={formData.description} onChange={handleChange} required className={fieldErrors.description ? 'input-error' : ''} placeholder="مثلا: حقوق فروردین، فروش کالا، هدیه"/>
                 {fieldErrors.description && <span className="field-error">{fieldErrors.description}</span>}
            </div>

            <div className="form-group">
                <label htmlFor="receiveTime">زمان دریافت (اختیاری):</label>
                <input type="datetime-local" id="receiveTime" name="receiveTime" value={formData.receiveTime} onChange={handleChange} />
                 <small>اگر خالی بگذارید، زمان فعلی ثبت می‌شود.</small>
            </div>

          
            <button type="submit" disabled={isSaving } className="submit-btn">
                {isSaving ? 'در حال ثبت...' : 'ثبت دریافت'}
            </button>
        </form>
    );
};

export default ReceiveForm;