package com.paymentchain.transaction.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Transaction {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long id;
    private String reference;
    private String accountIban;
    private Date date;
    private double amount;
    private double fee;
    private String description;
    @Enumerated(EnumType.ORDINAL)
    private Status status;
    @Enumerated(EnumType.ORDINAL)
    private Channel channel;

}
