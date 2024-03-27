package com.paymentchain.transaction.services;

import com.paymentchain.transaction.entities.Channel;
import com.paymentchain.transaction.entities.Status;
import com.paymentchain.transaction.entities.Transaction;
import com.paymentchain.transaction.entities.vo.TransactionVO;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TransactionService {
    public Transaction Create(TransactionVO transaction) {
        Transaction newtransaction = new Transaction();
        newtransaction.setAccountIban(transaction.getAccountIban());
        newtransaction.setFee(transaction.getFee());
        newtransaction.setDate(transaction.getDate());
        newtransaction.setChannel(this.getChannel(transaction.getChannel()));
        newtransaction.setAmount(this.applyFee(transaction.getAmount(), transaction.getFee()));
        newtransaction.setReference(transaction.getReference());
        newtransaction.setDescription(transaction.getDescription());
        newtransaction.setStatus(this.getStatusFromDate(transaction.getDate()));
        return newtransaction;
    }

    private double applyFee(double amount, double fee){
        if (fee > 0){
            return amount - (fee * amount / 100);
        }
        return amount;
    }

    private Status getStatusFromDate(Date date){
       Date currentDate = new Date();
       if (date.before(currentDate)){
           return Status.LIQUIDATED;
       }
        return Status.PENDING;
    }

    private Channel getChannel(String name){
        return switch (name) {
            case "office" -> Channel.OFFICE;
            case "atm" -> Channel.ATM;
            default -> Channel.WEB;
        };
    }
}
