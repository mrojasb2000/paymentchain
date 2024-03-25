package com.paymentchain.customer.controllers;

import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping()
    public List<Customer> list() {
        return customerRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    public ResponseEntity<?> get(@RequestParam("id") long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    public ResponseEntity<?> put(@RequestParam("id") long id, @RequestBody Customer input) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
           Customer newcustomer = customer.get();
           newcustomer.setCode(input.getCode());
           newcustomer.setNames(input.getNames());
           newcustomer.setSurname(input.getSurname());
           newcustomer.setIban(input.getIban());
           newcustomer.setPhone(input.getPhone());
           newcustomer.setAddress(input.getAddress());

           Customer save = customerRepository.save(newcustomer);
           return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> post(@RequestBody Customer input) {
        Customer customer = customerRepository.save(input);
        return ResponseEntity.ok(customer);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<?> delete(@RequestParam("id") long id) {
        customerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
