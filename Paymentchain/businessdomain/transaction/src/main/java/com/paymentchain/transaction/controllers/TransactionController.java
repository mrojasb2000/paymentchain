package com.paymentchain.transaction.controllers;

import com.paymentchain.transaction.entities.AmountEqualZeroException;
import com.paymentchain.transaction.entities.Transaction;
import com.paymentchain.transaction.entities.vo.TransactionVO;
import com.paymentchain.transaction.repositories.TransactionRepository;
import com.paymentchain.transaction.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping("/")
    public List<Transaction> list(){
        return transactionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id")  long id){
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()){
            return new ResponseEntity<>(transaction, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/customer/transaction/{iban}")
    public Transaction get(@PathVariable("iban") String accountIban) {
        return transactionRepository.findByAccountIban(accountIban);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody TransactionVO input) throws AmountEqualZeroException  {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()){
            Transaction newtransaction = create(input);
            return new ResponseEntity<>(newtransaction, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody TransactionVO input) throws AmountEqualZeroException {
        Transaction transaction = transactionRepository.save(create(input));
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()){
            transactionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private Transaction create(TransactionVO transactionVO) throws AmountEqualZeroException {
        if (transactionVO.getAmount() <= 0){
            throw new AmountEqualZeroException("Amount equal zero value");
        }
        return transactionService.Create(transactionVO);
    }


}
