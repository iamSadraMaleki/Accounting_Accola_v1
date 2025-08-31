package com.example.project.transection;



public class CardLookupResponse {

    private String cardHolderName;


    public CardLookupResponse() {
    }

    public CardLookupResponse(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }



    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }


}