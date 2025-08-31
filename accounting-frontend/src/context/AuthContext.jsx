import React, { createContext, useState, useEffect, useContext } from 'react';
import axios from 'axios'; 


const AuthContext = createContext(null);


export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null); 
    const [token, setToken] = useState(localStorage.getItem('authToken') || null); 
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('authToken')); 
    const [isLoading, setIsLoading] = useState(true); 
    
    useEffect(() => {
        const storedToken = localStorage.getItem('authToken');
        const storedUser = localStorage.getItem('user');

        if (storedToken && storedUser) {
            try {
                setToken(storedToken);
                setUser(JSON.parse(storedUser));
                setIsLoggedIn(true);
               
                axios.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
            } catch (error) {
                
                console.error("Error parsing stored user data:", error);
                logout(); 
            }
        }
        setIsLoading(false); 
    }, []);

   
    const login = (loginData) => { 
        if (loginData && loginData.token && loginData.username) {
             
            const authToken = loginData.token;
            const userData = {
                id: loginData.id,
                username: loginData.username,
                email: loginData.email,
                role: loginData.role
            };

            localStorage.setItem('authToken', authToken);
            localStorage.setItem('user', JSON.stringify(userData));
            axios.defaults.headers.common['Authorization'] = `Bearer ${authToken}`;

            setToken(authToken);
            setUser(userData);
            setIsLoggedIn(true);
            console.log("User logged in via context:", userData);
        } else {
            console.error("Login function called with invalid data:", loginData);
        }

    };


    const logout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        delete axios.defaults.headers.common['Authorization']; 

        setToken(null);
        setUser(null);
        setIsLoggedIn(false);
        console.log("User logged out via context");
        
    };

    
    const value = {
        token,
        user,
        isLoggedIn,
        isLoading, 
        login,
        logout,
    };

    return (
        <AuthContext.Provider value={value}>
            {children} {}
        </AuthContext.Provider>
    );
};


export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

