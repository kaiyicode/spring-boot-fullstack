package com.kaiyicode;

import com.github.javafaker.Faker;
import com.kaiyicode.customer.Customer;
import com.kaiyicode.customer.CustomerRepository;
import com.kaiyicode.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        System.out.println("Application started");
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            Faker faker = new Faker();
            Random random = new Random();
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            int age = random.nextInt(16, 99);
            Gender gender = age % 2 == 0 ? Gender.MALE : Gender.FEMALE;
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    firstName + "." + lastName + "@gmail.com",
                    age,
                    gender);
            customerRepository.save(customer);
        };
    }
}