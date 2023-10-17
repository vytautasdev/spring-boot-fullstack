package com.vytautasdev.customerdatabase.customer;

import com.vytautasdev.customerdatabase.exception.DuplicateResourceException;
import com.vytautasdev.customerdatabase.exception.RequestValidationException;
import com.vytautasdev.customerdatabase.exception.ResourceNotFoundException;
import org.junit.jupiter.api.AfterEach;
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
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        verify(customerDao).getAllCustomers();
    }

    @Test
    void canGetCustomer() {
        // Given
        var id = 10L;
        var customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        // When
        var actual = underTest.getCustomer(id);

        // Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        // Given
        var id = 10L;
        when(customerDao.getCustomerById(id)).thenReturn(Optional.empty());

        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found.".formatted(id));
    }

    @Test
    void addCustomer() {
        // Given
        var email = "alex@gmail.com";
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(false);

        var request = new CustomerRegistrationRequest("Alex", email, 19);

        // When
        underTest.addCustomer(request);

        // Then
        var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).addCustomer(customerArgumentCaptor.capture());

        var capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
    }

    @Test
    void willThrowWhenEmailExistsWhileAddingACustomer() {
        // Given
        var email = "alex@gmail.com";
        when(customerDao.existsCustomerWithEmail(email)).thenReturn(true);

        var request = new CustomerRegistrationRequest("Alex", email, 19);

        // When
        assertThatThrownBy(() -> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("This email is already taken.");

        // Then
        verify(customerDao, never()).addCustomer(any());

    }

    @Test
    void deleteCustomerById() {
        // Given
        var id = 10L;
        when(customerDao.existsCustomerWithId(id)).thenReturn(true);

        // When
        underTest.deleteCustomerById(id);

        // Then
        verify(customerDao).deleteCustomerById(id);
    }

    @Test
    void willThrowDeleteCustomerByIdNotExists() {
        // Given
        var id = 10L;
        when(customerDao.existsCustomerWithId(id)).thenReturn(false);


        // When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Customer with id [%s] not found.".formatted(id));

        // Then
        verify(customerDao, never()).deleteCustomerById(id);

    }

    @Test
    void canUpdateAllCustomersProperties() {
        // Given
        var id = 10L;
        var customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        var newEmail = "alexandro@gmail.com";
        var updateRequest = new CustomerUpdateRequest(customer.getName(), newEmail, customer.getAge());

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        var capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getEmail()).isEqualTo(updateRequest.email());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
    }

    @Test
    void canUpdateOnlyCustomerName() {
        // Given
        var id = 10L;
        var customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        var updateRequest = new CustomerUpdateRequest("Alexandro", null, null);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        var capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(updateRequest.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // Given
        var id = 10L;
        var customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        var newEmail = "alexandro@gmail.com";
        var updateRequest = new CustomerUpdateRequest(null, newEmail, null);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        var capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // Given
        var id = 10L;
        var customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        var updateRequest = new CustomerUpdateRequest(null, null, 22);

        // When
        underTest.updateCustomer(id, updateRequest);

        // Then
        var customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        var capturedCustomer = customerArgumentCaptor.getValue();

        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(updateRequest.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
    }

    @Test
    void willThrowWhenTryingToUpdateCustomerEmailWhenAlreadyTaken() {
        // Given
        var id = 10L;
        var customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        var newEmail = "alexandro@gmail.com";
        var updateRequest = new CustomerUpdateRequest(null, newEmail, null);

        when(customerDao.existsCustomerWithEmail(newEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("This email is already taken.");


        // Then
        verify(customerDao, never()).updateCustomer(any());
    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // Given
        var id = 10L;
        var customer = new Customer(id, "Alex", "alex@gmail.com", 19);
        when(customerDao.getCustomerById(id)).thenReturn(Optional.of(customer));

        var updateRequest = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, updateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found.");

        // Then
        verify(customerDao, never()).updateCustomer(any());


    }
}