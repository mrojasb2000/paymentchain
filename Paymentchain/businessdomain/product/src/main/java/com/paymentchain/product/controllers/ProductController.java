package com.paymentchain.product.controllers;

import com.paymentchain.product.entities.Product;
import com.paymentchain.product.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping()
    public List<Product> list(){
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody Product input){
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            Product newproduct = product.get();
            newproduct.setCode(input.getCode());
            newproduct.setName(input.getName());
            Product save = productRepository.save(newproduct);
            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping()
    public ResponseEntity<?> post(@RequestBody Product input){
        Product product = productRepository.save(input);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id){
        productRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

