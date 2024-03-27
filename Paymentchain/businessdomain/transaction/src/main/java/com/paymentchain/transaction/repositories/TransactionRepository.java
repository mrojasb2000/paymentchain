package com.paymentchain.transaction.repositories;

import com.paymentchain.transaction.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT c FROM Transaction c WHERE c.accountIban = ?1")
    public Transaction findByAccountIban(String iban);
}
