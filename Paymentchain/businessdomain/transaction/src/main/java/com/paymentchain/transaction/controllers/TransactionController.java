package com.paymentchain.transaction.controllers;

import com.paymentchain.transaction.entities.Status;
import com.paymentchain.transaction.entities.Channel;
import com.paymentchain.transaction.entities.Transaction;
import com.paymentchain.transaction.entities.vo.TransactionVO;
import com.paymentchain.transaction.repositories.TransactionRepository;
import com.paymentchain.transaction.services.ApplyFeeService;
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
    private ApplyFeeService applyFeeService;
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

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody TransactionVO input) {
        Optional<Transaction> transaction = transactionRepository.findById(id);
        if (transaction.isPresent()){
            Transaction newtransaction = create(input);
            return new ResponseEntity<>(newtransaction, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody TransactionVO input) {
        Transaction transaction = transactionRepository.save(create(input));
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        transactionRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Transaction create(TransactionVO transactionVO) {
        Transaction newtransaction = new Transaction();
        newtransaction.setAccountIban(transactionVO.getAccountIban());
        newtransaction.setFee(transactionVO.getFee());
        newtransaction.setDate(transactionVO.getDate());
        newtransaction.setChannel(getChannel(transactionVO.getChannel()));
        newtransaction.setAmount(applyFeeService.Apply(transactionVO.getAmount(), transactionVO.getFee()));
        newtransaction.setReference(transactionVO.getReference());
        newtransaction.setDescription(transactionVO.getDescription());
        newtransaction.setStatus(getStatus(transactionVO.getStatus()));
        return newtransaction;
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
