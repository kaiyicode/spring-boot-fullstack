package com.kaiyicode.journey;

import com.github.javafaker.Faker;
import com.kaiyicode.customer.Customer;
import com.kaiyicode.customer.CustomerRegistrationRequest;
import com.kaiyicode.customer.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webClient;
    private static final Random RANDOM = new Random();
    private static final String CUSTOMER_URI = "api/v1/customer";

    @Test
    void canRegisterCustomer() {
        // create registration request
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String name = firstName + " " + lastName;
        String email = firstName + "." + lastName + "-" + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(16, 99);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        // send a post request
        webClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();

        // get all customers
        List<Customer> allCustomers = webClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(
                request.name(), request.email(), request.age()
        );

        // make sure that customer is present
        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);

        // get customer by id
        var id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        expectedCustomer.setId(id);

        webClient.get()
                .uri(CUSTOMER_URI + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);
    }

    @Test
    void canDeleteCustomer() {
        // create registration request
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String name = firstName + " " + lastName;
        String email = firstName + "." + lastName + "-" + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(16, 99);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        // send a post request
        webClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();

        // get all customers
        List<Customer> allCustomers = webClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // delete customer by id
        webClient.delete()
                .uri(CUSTOMER_URI + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        // get customer by id
        webClient.get()
                .uri(CUSTOMER_URI + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        // create registration request
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String lastName = faker.name().lastName();
        String name = firstName + " " + lastName;
        String email = firstName + "." + lastName + "-" + UUID.randomUUID() + "@gmail.com";
        int age = RANDOM.nextInt(16, 99);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );

        // send a post request
        webClient.post()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();

        // get all customers
        List<Customer> allCustomers = webClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult()
                .getResponseBody();

        var id = allCustomers.stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // update customer
        String newFirstName = faker.name().firstName();
        String newLastName = faker.name().lastName();
        String newName = newFirstName + " " + newLastName;
        String newEmail = newFirstName + "." + newLastName + "-" + UUID.randomUUID() + "@gmail.com";
        int newAge = RANDOM.nextInt(16, 99);

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(
                newName, newEmail, newAge
        );

        webClient.put()
                .uri(CUSTOMER_URI + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerUpdateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus().isOk();

        // get customer by id
        Customer expectedCustomer = new Customer(
                id, newName, newEmail, newAge
        );
        webClient.get()
                .uri(CUSTOMER_URI + "/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);
    }
}
