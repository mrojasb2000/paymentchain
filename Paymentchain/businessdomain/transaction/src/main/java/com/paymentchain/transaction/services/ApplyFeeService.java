package com.paymentchain.transaction.services;

import org.springframework.stereotype.Service;

@Service
public class ApplyFeeService {
    public double Apply(double amount, double fee){
        if (fee > 0){
            return ApplyFee(amount, fee);
        }
        return amount;
    }

    private double ApplyFee(double amount, double fee){
        return amount - (fee * amount / 100);
    }
}
