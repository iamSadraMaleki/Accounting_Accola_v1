
import React, { useState, useEffect, useCallback } from 'react'; 
import CardListComponent from '../components/CardListComponent.jsx';
import BalanceStatusComponent from '../components/BalanceStatusComponent.jsx';
import CardFormComponent from '../components/CardFormComponent.jsx'; 
import apiClient from '../api/axiosConfig'; 
import './CardManagementPage.css';

const CardManagementPage = () => {
    const [showAddForm, setShowAddForm] = useState(false); 

   
    const [cards, setCards] = useState([]);
    const [balances, setBalances] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    
    const fetchData = useCallback(async () => {
        setIsLoading(true);
        setError('');
        try {
            
            const [cardsResponse, balancesResponse] = await Promise.all([
                apiClient.get('/api/cards'),
                apiClient.get('/api/cards/balance-status')
            ]);
            setCards(cardsResponse.data || []);
            setBalances(balancesResponse.data || []);
        } catch (err) {
            console.error("Error fetching card data:", err);
            setError("خطا در دریافت اطلاعات کارت‌ها.");
        } finally {
            setIsLoading(false);
        }
    }, []); 

   
    useEffect(() => {
        fetchData();
    }, [fetchData]); 
    
    const handleSaveCard = () => {
        setShowAddForm(false); 
        
        fetchData(); 
    };

    
    const handleDeleteCard = async (cardId) => {
        if (window.confirm('آیا از حذف این کارت مطمئن هستید؟')) {
            try {
                await apiClient.delete(`/api/cards/${cardId}`);
                fetchData(); 
            } catch (err) {
                 console.error("Error deleting card:", err);
                
                 alert('خطا در حذف کارت.');
            }
        }
    };


    return (
        <div className="card-management-container">
            <h2>مدیریت کارت‌های بانکی</h2>

            <div className="card-actions">
                
                <button onClick={() => setShowAddForm(true)}>
                    افزودن کارت جدید
                </button>
            </div>

            
            {showAddForm && (
                <CardFormComponent
                    onSave={handleSaveCard} 
                    onCancel={() => setShowAddForm(false)} 
                />
            )}

           
             {isLoading && <p>در حال بارگذاری داده‌ها...</p>}
             {error && <p className="error-message">{error}</p>}

            
            {!isLoading && !error && (
                <>
                    <CardListComponent
                        cards={cards} 
                       
                        onDelete={handleDeleteCard} 
                    />
                    <BalanceStatusComponent balances={balances} /> 
                 </>
            )}

        </div>
    );
};


export default CardManagementPage;