package com.paymentchain.transaction.entities;

public class AmountEqualZeroException extends Exception {
    public AmountEqualZeroException(String message){
        super(message);
    }
}
