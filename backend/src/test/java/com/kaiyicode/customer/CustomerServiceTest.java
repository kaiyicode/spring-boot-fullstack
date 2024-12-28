package com.kaiyicode.customer;

import com.kaiyicode.exception.DuplicateResourceException;
import com.kaiyicode.exception.NoDataChangeException;
import com.kaiyicode.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDAO customerDAO;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDAO);
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDAO).selectAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        Customer actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        // Given
        int id = 10;
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(
                        "customer with [%s] not found".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        String email = "alex@gmail.com";
        when(customerDAO.existsCustomerWithEmail(email)).thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19
        );

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());

        Customer customer = customerArgumentCaptor.getValue();

        assertThat(customer.getId()).isNull();
        assertThat(customer.getName()).isEqualTo(request.name());
        assertThat(customer.getEmail()).isEqualTo(request.email());
        assertThat(customer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {
        // Given
        String email = "alex@gmail.com";
        when(customerDAO.existsCustomerWithEmail(email)).thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Alex", email, 19
        );

        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email address already exists");

        // Then
        verify(customerDAO, never()).insertCustomer(any());
    }

    @Test
    void deleteCustomerById() {
        // Given
        int id = 10;
        when(customerDAO.existsCustomerWithId(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void willThrowWhenDeleteCustomerByIdNotExists() {
        // Given
        int id = 10;
        when(customerDAO.existsCustomerWithId(id)).thenReturn(false);

        // When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with [%s] not found".formatted(id));

        // Then
        verify(customerDAO, never()).deleteCustomerById(id);
    }

    @Test
    void updateCustomerName() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Alex1", null, null
        );

        // When
        underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer actual = customerArgumentCaptor.getValue();

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo(request.name());
        assertThat(actual.getEmail()).isEqualTo(customer.getEmail());
        assertThat(actual.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void updateCustomerEmail() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, "alex1@gmail.com", null
        );
        when(customerDAO.existsCustomerWithEmail("alex1@gmail.com")).thenReturn(false);

        // When
        underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer actual = customerArgumentCaptor.getValue();

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo(customer.getName());
        assertThat(actual.getEmail()).isEqualTo(request.email());
        assertThat(actual.getAge()).isEqualTo(customer.getAge());
    }

    @Test
    void willThrowWhenEmailExistsWhileUpdateCustomerEmail() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, "alex1@gmail.com", null
        );
        when(customerDAO.existsCustomerWithEmail("alex1@gmail.com")).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("email address already exists");

        // Then
        verify(customerDAO, never()).updateCustomer(any());
    }

    @Test
    void updateCustomerAge() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                null, null, 20
        );

        // When
        underTest.updateCustomer(id, request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());

        Customer actual = customerArgumentCaptor.getValue();

        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo(customer.getName());
        assertThat(actual.getEmail()).isEqualTo(customer.getEmail());
        assertThat(actual.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenUpdateCustomerNotExists() {
        // Given
        int id = 10;
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.empty());
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Alex", "alex@gmail.com", 19
        );

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with [%s] not found".formatted(id));

        // Then
        verify(customerDAO, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenUpdateCustomerNoChanges() {
        // Given
        int id = 10;
        Customer customer = new Customer(
                id, "Alex", "alex@gmail.com", 19
        );
        when(customerDAO.selectCustomerById(id)).thenReturn(Optional.of(customer));
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "Alex", "alex@gmail.com", 19
        );

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, request))
                .isInstanceOf(NoDataChangeException.class)
                .hasMessage("no data changes found");

        // Then
        verify(customerDAO, never()).updateCustomer(any());
    }
}