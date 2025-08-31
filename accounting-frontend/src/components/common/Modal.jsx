
import React from 'react';
import './Modal.css'; 

const Modal = ({ isOpen, onClose, title, children }) => {
    if (!isOpen) {
        return null; 
    }

   
    const handleContentClick = (e) => {
        e.stopPropagation();
    };

    return (
       
        <div className="modal-overlay" onClick={onClose}>
            
            <div className="modal-content" onClick={handleContentClick}>
                <div className="modal-header">
                    <h4 className="modal-title">{title || 'پیام'}</h4>
                   
                    <button className="modal-close-btn" onClick={onClose}>
                        &times; 
                    </button>
                </div>
                <div className="modal-body">
                    {children}
                </div>
            </div>
        </div>
    );
};

export default Modal;