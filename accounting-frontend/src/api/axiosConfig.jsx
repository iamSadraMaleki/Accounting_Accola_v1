
import axios from 'axios';


const apiClient = axios.create({
  
  baseURL: 'http://localhost:8080',

  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json', 
  },
});


apiClient.interceptors.request.use(
  (config) => {
    
    const token = localStorage.getItem('authToken');
    

   
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
     
    } else {
      console.log('Interceptor: No token found in localStorage.'); 
    }

    console.log(`[Request Interceptor] Requesting: ${config.method.toUpperCase()} ${config.url}`);
    console.log(`[Request Interceptor] Token from localStorage: ${token ? 'Found (' + token.substring(0, 10) + '...)' : 'Not Found'}`);
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
      console.log('[Request Interceptor] Authorization header SET');
    } else {
      console.log('[Request Interceptor] Authorization header NOT SET');
    }


   
    return config; 
  },
  (error) => {
   
    console.error('Interceptor Request Error:', error);
    return Promise.reject(error); 
  }
);


apiClient.interceptors.response.use(
  (response) => {
   
    return response;
  },
  (error) => {
    
    console.error('Interceptor Response Error:', error.response || error.message);

    
    if (error.response && error.response.status === 401) {
      console.error('Interceptor: Unauthorized (401). Token may be invalid/expired.');

     
    }

  
    return Promise.reject(error);
  }
);

export default apiClient;

