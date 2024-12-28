package com.kaiyicode.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Integer id);
    void insertCustomer(Customer customer);
    boolean existsCustomerWithEmail(String email);
    void deleteCustomerById(Integer id);
    boolean existsCustomerWithId(Integer id);
    void updateCustomer(Customer customer);
}
