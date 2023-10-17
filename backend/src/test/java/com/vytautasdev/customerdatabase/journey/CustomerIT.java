package com.vytautasdev.customerdatabase.journey;

import com.github.javafaker.Faker;
import com.vytautasdev.customerdatabase.customer.Customer;
import com.vytautasdev.customerdatabase.customer.CustomerRegistrationRequest;
import com.vytautasdev.customerdatabase.customer.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.*;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIT {

    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM = new Random();
    private static final String CUSTOMERS_URI = "/api/v1/customers/";


    @Test
    void canRegisterACustomer() {
        // create registration request
        var faker = new Faker();
        var fakerName = faker.name();

        var name = fakerName.fullName();
        var email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
        var age = RANDOM.nextInt(1, 99);

        var request = new CustomerRegistrationRequest(name, email, age);

        // send a post request

        webTestClient.post()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        var allCustomers = webTestClient.get()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        // make sure that customer is present
        var expectedCustomer = new Customer(name, email, age);

        assertThat(allCustomers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);

        // get customer by Id
        webTestClient.get()
                .uri(CUSTOMERS_URI + "{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // create registration request
        var faker = new Faker();
        var fakerName = faker.name();

        var name = fakerName.fullName();
        var email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
        var age = RANDOM.nextInt(1, 99);

        var request = new CustomerRegistrationRequest(name, email, age);

        // send a post request
        webTestClient.post()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        var allCustomers = webTestClient.get()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // delete customer
        webTestClient.delete()
                .uri(CUSTOMERS_URI + "{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by Id
        webTestClient.get()
                .uri(CUSTOMERS_URI + "{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // create registration request
        var faker = new Faker();
        var fakerName = faker.name();

        var name = fakerName.fullName();
        var email = fakerName.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
        var age = RANDOM.nextInt(1, 99);

        var request = new CustomerRegistrationRequest(name, email, age);

        // send a post request
        webTestClient.post()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get all customers
        var allCustomers = webTestClient.get()
                .uri(CUSTOMERS_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // update customer
        var newName = "newName";
        var updateRequest = new CustomerUpdateRequest(
                newName, null, null
        );

        webTestClient.put()
                .uri(CUSTOMERS_URI + "{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        // get customer by Id
        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMERS_URI + "{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        var expected = new Customer(id, newName, email, age);

        assertThat(updatedCustomer).isEqualTo(expected);

    }
}
