package com.paymentchain.transaction.services;

import com.paymentchain.transaction.entities.Channel;
import com.paymentchain.transaction.entities.Status;
import com.paymentchain.transaction.entities.Transaction;
import com.paymentchain.transaction.entities.vo.TransactionVO;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    public Transaction Create(TransactionVO transaction) {
        Transaction newtransaction = new Transaction();
        newtransaction.setAccountIban(transaction.getAccountIban());
        newtransaction.setFee(transaction.getFee());
        newtransaction.setDate(transaction.getDate());
        newtransaction.setChannel(getChannel(transaction.getChannel()));
        newtransaction.setAmount(this.applyFee(transaction.getAmount(), transaction.getFee()));
        newtransaction.setReference(transaction.getReference());
        newtransaction.setDescription(transaction.getDescription());
        newtransaction.setStatus(getStatus(transaction.getStatus()));
        return newtransaction;
    }

    private double applyFee(double amount, double fee){
        if (fee > 0){
            return amount - (fee * amount / 100);
        }
        return amount;
    }

    private Status getStatus(String name){
        return switch (name) {
            case "liquidated" -> Status.LIQUIDATED;
            case "reject" -> Status.REJECT;
            case "cancelled" -> Status.CANCELLED;
            default -> Status.PENDING;
        };
    }

    private Channel getChannel(String name){
        return switch (name) {
            case "office" -> Channel.OFFICE;
            case "atm" -> Channel.ATM;
            default -> Channel.WEB;
        };
    }
}
