package com.paymentchain.customer.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CustomerProduct {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private long id;
    private long productId;
    @Transient
    private String productName;
    @JsonIgnore // it is necessary for avoid infinite recursion
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Customer.class)
    @JoinColumn(name = "customerId", nullable = true)
    private Customer customer;


}
