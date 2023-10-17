package com.vytautasdev.customerdatabase.customer;

import com.vytautasdev.customerdatabase.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCDataAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void getAllCustomers() {
        // Given
        var customer = new Customer(
                FAKER.name().fullName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        underTest.addCustomer(customer);

        // When
        var actual = underTest.getAllCustomers();

        // Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void getCustomerById() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var customer = new Customer(
                FAKER.name().fullName(),
                email,
                20
        );
        underTest.addCustomer(customer);
        var id = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var actual = underTest.getCustomerById(id);

        // Then
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void willReturnEmptyWhenGetCustomerById() {
        // Given
        var id = 0L;

        // When
        var actual = underTest.getCustomerById(id);

        // Then
        assertThat(actual).isEmpty();

    }

    @Test
    void addCustomer() {
        // Given

        // When

        // Then
    }

    @Test
    void existsCustomerWithEmail() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var name = FAKER.name().fullName();

        var customer = new Customer(
                name,
                email,
                20
        );

        underTest.addCustomer(customer);

        // When
        var actual = underTest.existsCustomerWithEmail(email);

        // Then
        assertThat(actual).isTrue();

    }

    @Test
    void existsCustomerWithEmailReturnsFalseWhenDoesNotExist() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        var actual = underTest.existsCustomerWithEmail(email);

        // Then
        assertThat(actual).isFalse();

    }

    @Test
    void existsCustomerWithId() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var name = FAKER.name().fullName();

        var customer = new Customer(
                name,
                email,
                20
        );

        underTest.addCustomer(customer);

        var id = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var actual = underTest.existsCustomerWithId(id);

        // Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomerWithIdWillReturnFalseWhenIdNotPresent() {
        // Given
        var id = -1L;

        // When
        var actual = underTest.existsCustomerWithId(id);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var name = FAKER.name().fullName();

        var customer = new Customer(
                name,
                email,
                20
        );

        underTest.addCustomer(customer);

        var id = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        // When
        underTest.deleteCustomerById(id);

        // Then
        var actual = underTest.getCustomerById(id);
        assertThat(actual).isNotPresent();
    }

    @Test
    void updateCustomerName() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var name = FAKER.name().fullName();

        var customer = new Customer(
                name,
                email,
                20
        );

        underTest.addCustomer(customer);

        var id = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newName = "foo";

        // When
        var updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setName(newName);

        underTest.updateCustomer(updatedCustomer);

        // Then
        var actual = underTest.getCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(newName);
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerEmail() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var name = FAKER.name().fullName();

        var customer = new Customer(
                name,
                email,
                20
        );

        underTest.addCustomer(customer);

        var id = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newEmail = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        var updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setEmail(newEmail);

        underTest.updateCustomer(updatedCustomer);

        // Then
        var actual = underTest.getCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getEmail()).isEqualTo(newEmail);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });
    }

    @Test
    void updateCustomerAge() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var name = FAKER.name().fullName();

        var customer = new Customer(
                name,
                email,
                20
        );

        underTest.addCustomer(customer);

        var id = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        var newAge = 100;

        // When
        var updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setAge(newAge);

        underTest.updateCustomer(updatedCustomer);

        // Then
        var actual = underTest.getCustomerById(id);

        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getAge()).isEqualTo(newAge);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
        });
    }


    @Test
    void willUpdateAllPropertiesCustomer() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var name = FAKER.name().fullName();

        var customer = new Customer(
                name,
                email,
                20
        );

        underTest.addCustomer(customer);

        var id = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var updatedCustomer = new Customer();
        updatedCustomer.setId(id);
        updatedCustomer.setName("foo");
        updatedCustomer.setEmail(UUID.randomUUID().toString());
        updatedCustomer.setAge(22);

        underTest.updateCustomer(updatedCustomer);

        // Then
        var actual = underTest.getCustomerById(id);
        assertThat(actual).isPresent().hasValue(updatedCustomer);
    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        // Given
        var email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        var name = FAKER.name().fullName();

        var customer = new Customer(
                name,
                email,
                20
        );

        underTest.addCustomer(customer);

        var id = underTest.getAllCustomers()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        var updatedCustomer = new Customer();
        updatedCustomer.setId(id);

        underTest.updateCustomer(updatedCustomer);

        // Then
        var actual = underTest.getCustomerById(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getAge()).isEqualTo(customer.getAge());
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
        });
    }
}