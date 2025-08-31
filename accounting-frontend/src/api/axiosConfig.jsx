import axios from 'axios';

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api', 
  // 👆 اگه ENV تنظیم شد → از اون استفاده کن، وگرنه پیش‌فرض /api
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  },
});

// Interceptor درخواست‌ها
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');

    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
      console.log('[Request Interceptor] Authorization header SET');
    } else {
      console.log('[Request Interceptor] Authorization header NOT SET');
    }

    console.log(`[Request Interceptor] ${config.method.toUpperCase()} ${config.url}`);
    return config;
  },
  (error) => {
    console.error('Interceptor Request Error:', error);
    return Promise.reject(error);
  }
);

// Interceptor پاسخ‌ها
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error('Interceptor Response Error:', error.response || error.message);

    if (error.response && error.response.status === 401) {
      console.error('Interceptor: Unauthorized (401). Token may be invalid/expired.');
      // 👉 میتونی اینجا redirect به صفحه login هم بذاری
    }

    return Promise.reject(error);
  }
);

export default apiClient;
