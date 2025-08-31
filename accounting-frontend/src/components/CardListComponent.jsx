
import React from 'react'; 
import './CardListComponent.css';


const CardItem = ({ card, onEditClick, onDeleteClick }) => {
    const formatExpiry = (month, year) => {
        if (!month || !year) return 'N/A';
        const monthStr = String(month).padStart(2, '0');
        const yearStr = String(year).slice(-2);
        return `${monthStr}/${yearStr}`;
    }

    return (
        <div className="card-item">
            <div className="card-header">
                <span className="bank-name">{card.bankName}</span>
                <span className="card-expiry">انقضا: {formatExpiry(card.expiryMonth, card.expiryYear)}</span>
            </div>
            <div className="card-body">
                <div className="card-number">{card.maskedCardNumber}</div> 
                <div className="card-holder">{card.cardHolderName}</div>
            </div>
            <div className="card-footer">
               
                <button
                    className="edit-btn"
                    onClick={() => onEditClick(card)} 
                    title="ویرایش"
                   
                >
                    ویرایش
                </button>
                <button
                    className="delete-btn"
                    onClick={() => onDeleteClick(card.id)} 
                    title="حذف"
                   
                >
                    حذف
                </button>
            
            </div>
        </div>
    );
};



const CardListComponent = ({ cards, onEdit, onDelete }) => {

   
    if (!cards) {
        return null;
    }

    return (
        <div className="card-list-container">
            <h4>کارت‌های ثبت شده</h4>
          
            {cards.length === 0 ? (
                <p>هنوز کارتی ثبت نشده است.</p>
            ) : (
                <div className="card-list">
                    {cards.map(card => (
                        <CardItem
                            key={card.id} 
                            card={card}   
                            onEditClick={onEdit} 
                            onDeleteClick={onDelete} 
                        />
                    ))}
                </div>
            )}
        </div>
    );
};

export default CardListComponent;