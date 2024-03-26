package com.paymentchain.transaction.entities.vo;


import lombok.Data;

import java.util.Date;

@Data
public class TransactionVO {
    private String reference;
    private String accountIban;
    private Date date;
    private double amount;
    private double fee;
    private String description;
    private String status;
    private String channel;
}
