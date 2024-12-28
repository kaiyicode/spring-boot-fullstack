package com.kaiyicode.customer;

import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {
    @Test
    void mapRow() throws SQLException {
        // Given
        CustomerRowMapper underTest = new CustomerRowMapper();

        ResultSet resultSet = mock(ResultSet.class);
        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("John");
        when(resultSet.getString("email")).thenReturn("john@gmail.com");
        when(resultSet.getInt("age")).thenReturn(32);

        // When
        Customer actual = underTest.mapRow(resultSet, 1);

        // Then
        Customer expected = new Customer(
                1, "John", "john@gmail.com", 32
        );
        assertThat(actual).isEqualTo(expected);
    }
}