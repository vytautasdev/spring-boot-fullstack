package com.vytautasdev.customerdatabase;

import com.github.javafaker.Faker;
import com.vytautasdev.customerdatabase.customer.Customer;
import com.vytautasdev.customerdatabase.customer.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class CustomerDatabaseApplication {


/*
Program flow: The controller class sends data down to the service class -> Service class gets data from the Dao
 */

    public static void main(String[] args) {
        SpringApplication.run(CustomerDatabaseApplication.class, args);

    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            var random = new Random();
            var faker = new Faker();
            var name = faker.name();
            var firstName = name.firstName();
            var lastName = name.lastName();
            var email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@gmail.com";
            var fullName = firstName + " " + lastName;
            var customer = new Customer(fullName, email, random.nextInt(16, 99));

            customerRepository.save(customer);
        };
    }

    // testing finish @ lesson 174
    // lesson 219 done -> well done!
}
