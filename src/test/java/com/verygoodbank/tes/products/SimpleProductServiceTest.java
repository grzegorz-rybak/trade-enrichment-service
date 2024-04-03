package com.verygoodbank.tes.products;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SimpleProductServiceTest {

    @InjectMocks
    private SimpleProductService simpleProductService;

    @Test
    void productSearchingTest() {
        // Given
        simpleProductService.getNames().put(3L, "REPO Domestic");

        // When
        String result = simpleProductService.readProductName(3);

        // Then
        assertThat(result).contains("REPO Domestic");
    }

}
