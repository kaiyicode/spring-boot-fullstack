package com.kaiyicode.customer;

import com.kaiyicode.exception.DuplicateResourceException;
import com.kaiyicode.exception.NoDataChangeException;
import com.kaiyicode.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {
        return customerDAO.selectAllCustomers();
    }

    public Customer getCustomer(int id) {
        return customerDAO
                .selectCustomerById(id)
                .orElseThrow(() -> new ResourceNotFoundException("customer with [%s] not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        String email = customerRegistrationRequest.email();
        if (customerDAO.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException("email address already exists");
        }

        Customer customer =  new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );

        customerDAO.insertCustomer(customer);
    }

    public void deleteCustomerById(int id) {
        if (!customerDAO.existsCustomerWithId(id)) {
            throw new ResourceNotFoundException("customer with [%s] not found".formatted(id));
        }

        customerDAO.deleteCustomerById(id);
    }

    public void updateCustomer(int customerId, CustomerUpdateRequest customerUpdateRequest) {
        Customer customer = customerDAO
                .selectCustomerById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer with [%s] not found".formatted(customerId)));

        boolean changed = false;
        if (customerUpdateRequest.name() != null &&
                !customerUpdateRequest.name().equals(customer.getName())) {
            changed = true;
            customer.setName(customerUpdateRequest.name());
        }

        if (customerUpdateRequest.email() != null &&
                !customerUpdateRequest.email().equals(customer.getEmail())) {
            if (customerDAO.existsCustomerWithEmail(customerUpdateRequest.email())) {
                throw new DuplicateResourceException("email address already exists");
            }
            changed = true;
            customer.setEmail(customerUpdateRequest.email());
        }

        if (customerUpdateRequest.age() != null &&
                !customerUpdateRequest.age().equals(customer.getAge())) {
            changed = true;
            customer.setAge(customerUpdateRequest.age());
        }

        if (!changed) {
            throw new NoDataChangeException("no data changes found");
        }

        customerDAO.updateCustomer(customer);
    }
}
