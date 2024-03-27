package com.paymentchain.customer.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.repositories.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {

    private final WebClient.Builder webClientBuilder;

    public CustomerRestController(WebClient.Builder webClientBuilder){
        this.webClientBuilder = webClientBuilder;
    }

    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping()
    public List<Customer> list() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isPresent()) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody Customer input) {
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
        input.getProducts().forEach(p -> {
            p.setCustomer(input);
        });
        Customer customer = customerRepository.save(input);
        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        customerRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/full")
    public Customer getByCode(@RequestParam("code") String code){
        Customer customer = customerRepository.findByCode(code);
        customer.getProducts().forEach(p -> {
            p.setProductName(getProductNameById(p.getProductId()));
        });
        return customer;
    }

    private String getProductNameById(long id){
        String PRODUCT_URL = "http://localhost:8082/product";
        WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(PRODUCT_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", PRODUCT_URL))
                .build();
        JsonNode block = webClient.method(HttpMethod.GET).uri("/" + id)
                .retrieve().bodyToMono(JsonNode.class).block();
        assert block != null;
        return block.get("name").asText();
    }

    private List<?> getTransactionByIban(String iban){
        String TRANSACTION_URL = "http://localhost:8084/transaction";
        WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl(TRANSACTION_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", TRANSACTION_URL))
                .build();

        return webClient.method(HttpMethod.GET).uri((uriBuilder) -> uriBuilder
                .path("/customer/transactions")
                .queryParam("accountIban", iban)
                .build())
                .retrieve().bodyToFlux(Object.class).collectList().block();
    }
}
