import React, { useState } from 'react';

import apiClient from '../api/axiosConfig'; 
import { useNavigate, Link } from 'react-router-dom'; 
import './RegisterPage.css';

const RegisterPage = () => {
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    fullName: ''
  });
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const [fieldErrors, setFieldErrors] = useState({});

  const navigate = useNavigate();

  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
    if (fieldErrors[name]) {
      setFieldErrors(prevErrors => ({ ...prevErrors, [name]: null }));
    }
    setError('');
  };

  
  const validateForm = () => {
    const errors = {};
    if (!formData.username || formData.username.length < 3) {
      errors.username = 'نام کاربری باید حداقل ۳ کاراکتر باشد.';
    }
    if (!formData.email || !/\S+@\S+\.\S+/.test(formData.email)) {
      errors.email = 'فرمت ایمیل نامعتبر است.';
    }
    if (!formData.password || formData.password.length < 6) {
      errors.password = 'رمز عبور باید حداقل ۶ کاراکتر باشد.';
    }
    if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = 'رمز عبور و تکرار آن مطابقت ندارند.';
    }
    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };


  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccessMessage('');
    if (!validateForm()) return;
    setLoading(true);

    const registrationData = {
      username: formData.username,
      email: formData.email,
      password: formData.password,
      fullName: formData.fullName
    };

    try {
     
      const response = await apiClient.post('/api/auth/register', registrationData);

      setSuccessMessage(response.data || 'ثبت نام با موفقیت انجام شد. در حال هدایت به صفحه ورود...');
      setFormData({ username: '', email: '', password: '', confirmPassword: '', fullName: '' });
      setFieldErrors({});

      setTimeout(() => {
        navigate('/login');
      }, 2000);

    } catch (err) {
      setLoading(false); 
      if (err.response) {
        console.error('Registration Error:', err.response.data);
        setError(err.response.data || 'خطا در ثبت نام. لطفاً دوباره تلاش کنید.');
      } else if (err.request) {
        console.error('Network Error:', err.request);
        setError('خطا در برقراری ارتباط با سرور.');
      } else {
        console.error('Error:', err.message);
        setError('یک خطای پیش‌بینی نشده رخ داد.');
      }
    }
    
  };

  
  return (
 
    <div className="register-page-container">
      <form onSubmit={handleSubmit} className="register-form">
        <h2>ثبت نام در سیستم</h2>

        {error && <p className="error-message">{error}</p>}
        {successMessage && <p className="success-message">{successMessage}</p>}

        <div className="form-group">
          <label htmlFor="username">نام کاربری:</label>
          <input type="text" id="username" name="username" value={formData.username} onChange={handleChange} className={fieldErrors.username ? 'input-error' : ''} required />
          {fieldErrors.username && <span className="field-error">{fieldErrors.username}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="email">ایمیل:</label>
          <input type="email" id="email" name="email" value={formData.email} onChange={handleChange} className={fieldErrors.email ? 'input-error' : ''} required />
           {fieldErrors.email && <span className="field-error">{fieldErrors.email}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="password">رمز عبور:</label>
          <input type="password" id="password" name="password" value={formData.password} onChange={handleChange} className={fieldErrors.password ? 'input-error' : ''} required />
           {fieldErrors.password && <span className="field-error">{fieldErrors.password}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="confirmPassword">تکرار رمز عبور:</label>
          <input type="password" id="confirmPassword" name="confirmPassword" value={formData.confirmPassword} onChange={handleChange} className={fieldErrors.confirmPassword ? 'input-error' : ''} required />
          {fieldErrors.confirmPassword && <span className="field-error">{fieldErrors.confirmPassword}</span>}
        </div>

        <div className="form-group">
          <label htmlFor="fullName">نام کامل (اختیاری):</label>
          <input type="text" id="fullName" name="fullName" value={formData.fullName} onChange={handleChange} />
        </div>

        <button type="submit" disabled={loading} className="register-button">
          {loading ? 'در حال ثبت نام...' : 'ثبت نام'}
        </button>

        <div className="login-link"> 
          حساب کاربری دارید؟ <Link to="/login">وارد شوید</Link>
        </div>
      </form>
    </div>
  );
};

export default RegisterPage;