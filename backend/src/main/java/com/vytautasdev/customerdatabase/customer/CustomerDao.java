package com.vytautasdev.customerdatabase.customer;

import java.util.List;
import java.util.Optional;

//  !! Service classes are responsible for the business logic (N tier architecture --  DAO Layer ) !!

public interface CustomerDao {
    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Long id);

    void addCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);

    boolean existsCustomerWithId(Long id);

    void deleteCustomerById(Long customerId);

    void updateCustomer(Customer updatedCustomer);
}
