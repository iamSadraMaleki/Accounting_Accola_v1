
import React, { useState } from 'react';
import apiClient from '../api/axiosConfig'; 
import './ChangePasswordForm.css'; 

const ChangePasswordForm = () => {
    const [passwords, setPasswords] = useState({
        currentPassword: '',
        newPassword: '',
        confirmNewPassword: ''
    });
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [fieldErrors, setFieldErrors] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;
        setPasswords(prev => ({ ...prev, [name]: value }));
        setError('');
        setSuccessMessage('');
        
        if (fieldErrors[name]) {
            setFieldErrors(prev => ({ ...prev, [name]: null }));
        }
        if (name === 'newPassword' || name === 'confirmNewPassword') {
             setFieldErrors(prev => ({ ...prev, confirmNewPassword: null }));
        }
    };

    
    const validateForm = () => {
        const errors = {};
        if (!passwords.currentPassword) {
            errors.currentPassword = 'رمز عبور فعلی الزامی است.';
        }
        if (!passwords.newPassword || passwords.newPassword.length < 6) {
            errors.newPassword = 'رمز عبور جدید باید حداقل ۶ کاراکتر باشد.';
        }
        if (passwords.newPassword !== passwords.confirmNewPassword) {
            errors.confirmNewPassword = 'رمز عبور جدید و تکرار آن مطابقت ندارند.';
        }
        setFieldErrors(errors);
        return Object.keys(errors).length === 0;
    };


    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setSuccessMessage('');

        if (!validateForm()) return; 

        setIsLoading(true);

        try {
            const response = await apiClient.post('/api/profile/me/change-password', {
                currentPassword: passwords.currentPassword,
                newPassword: passwords.newPassword,
                confirmNewPassword: passwords.confirmNewPassword
            });
            setSuccessMessage(response.data || 'رمز عبور با موفقیت تغییر کرد.');
            
            setPasswords({ currentPassword: '', newPassword: '', confirmNewPassword: '' });
             setFieldErrors({});

        } catch (err) {
            console.error("Error changing password:", err);
             if (err.response && err.response.data) {
                 setError(err.response.data); 
             } else {
                  setError("خطا در تغییر رمز عبور.");
             }
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="password-form">
            <h3>تغییر رمز عبور</h3>

            {error && <p className="error-message">{error}</p>}
            {successMessage && <p className="success-message">{successMessage}</p>}

             <div className="form-group">
                <label htmlFor="currentPassword">رمز عبور فعلی:</label>
                <input
                    type="password"
                    id="currentPassword"
                    name="currentPassword"
                    value={passwords.currentPassword}
                    onChange={handleChange}
                    className={fieldErrors.currentPassword ? 'input-error' : ''}
                    required
                 />
                 {fieldErrors.currentPassword && <span className="field-error">{fieldErrors.currentPassword}</span>}
             </div>
            <div className="form-group">
                <label htmlFor="newPassword">رمز عبور جدید:</label>
                 <input
                    type="password"
                    id="newPassword"
                    name="newPassword"
                    value={passwords.newPassword}
                    onChange={handleChange}
                    className={fieldErrors.newPassword ? 'input-error' : ''}
                    required
                 />
                 {fieldErrors.newPassword && <span className="field-error">{fieldErrors.newPassword}</span>}
            </div>
            <div className="form-group">
                <label htmlFor="confirmNewPassword">تایید رمز عبور جدید:</label>
                 <input
                    type="password"
                    id="confirmNewPassword"
                    name="confirmNewPassword"
                    value={passwords.confirmNewPassword}
                    onChange={handleChange}
                    className={fieldErrors.confirmNewPassword ? 'input-error' : ''}
                    required
                 />
                 {fieldErrors.confirmNewPassword && <span className="field-error">{fieldErrors.confirmNewPassword}</span>}
            </div>

            <button type="submit" disabled={isLoading} className="save-button">
                {isLoading ? 'در حال تغییر...' : 'تغییر رمز عبور'}
            </button>
        </form>
    );
};

export default ChangePasswordForm;