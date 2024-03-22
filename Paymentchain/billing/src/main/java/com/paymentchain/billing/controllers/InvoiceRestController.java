package com.paymentchain.billing.controllers;

import com.paymentchain.billing.entities.Invoice;
import com.paymentchain.billing.repositories.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/billing")
public class InvoiceRestController {

    @Autowired
     InvoiceRepository invoiceRepository;

    @GetMapping()
    public List<Invoice> list(){
        return invoiceRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id){
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent()) {
            return new ResponseEntity<>(invoice, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable long id, @RequestBody Invoice input) {
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent()) {
            Invoice newinvoice = invoice.get();
            newinvoice.setDetail(input.getDetail());
            newinvoice.setAmount(input.getAmount());
            Invoice save = invoiceRepository.save(newinvoice);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Invoice input) {
        Invoice invoice = invoiceRepository.save(input);
        return ResponseEntity.ok(invoice);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id){
        invoiceRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
