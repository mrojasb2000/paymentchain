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

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<?> get(@RequestParam("id") long id){
        Optional<Invoice> invoice = invoiceRepository.findById(id);
        if (invoice.isPresent()) {
            return new ResponseEntity<>(invoice, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<?> put(@RequestParam("id") long id, @RequestBody Invoice input) {
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

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<?> delete(@RequestParam("id") long id){
        invoiceRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
