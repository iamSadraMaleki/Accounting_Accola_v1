
import React, { useState } from 'react';
import axios from 'axios'; 
import { useNavigate, Link } from 'react-router-dom'; 
import { useAuth } from '../context/AuthContext.jsx';
import './LoginPage.css'; 



const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
           
            const response = await axios.post('http://localhost:8080/api/auth/login', { username, password });


            console.log("Login successful:", response.data);
            login(response.data); 
            navigate('/'); 
        } catch (err) {
            console.error("Login error:", err);
            setLoading(false);
            if (err.response && err.response.status === 401) {
                setError(err.response.data || 'نام کاربری یا رمز عبور نامعتبر است.');
            } else if (err.request && !err.response) {
                 
                 setError('خطا در برقراری ارتباط با سرور یا آدرس یافت نشد.');
            }
             else if (err.response) {
                
                 setError(`خطا: ${err.response.data || err.response.statusText}`);
             }
            else {
                setError('خطا در ورود. لطفاً دوباره تلاش کنید.');
            }
        }
        
    };

    return (
        <div className="login-page-container"> 
            <form onSubmit={handleSubmit} className="login-form"> 
                <h2>ورود به سیستم حسابداری</h2>

                {error && <p className="error-message">{error}</p>}

                <div className="form-group">
                    <label htmlFor="username">نام کاربری:</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                        autoFocus
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="password">رمز عبور:</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>

                <button type="submit" disabled={loading} className="login-button">
                    {loading ? 'در حال ورود...' : 'ورود'}
                </button>

                
                <div className="register-link">
                    هنوز حساب کاربری ندارید؟ <Link to="/register">ثبت نام کنید</Link>
                </div>
            </form>
        </div>
    );
};

export default LoginPage;