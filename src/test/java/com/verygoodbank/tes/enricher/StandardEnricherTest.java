package com.verygoodbank.tes.enricher;

import com.verygoodbank.tes.products.ProductService;
import com.verygoodbank.tes.validators.TradeValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StandardEnricherTest {

    @Mock
    private ProductService productService;

    @Mock
    private TradeValidator timeStampValidator;

    @InjectMocks
    private StandardEnricher standardEnricher;

    @Test
    void executeValidatorDuringEnrichmentProcess() {
        // Given
        final String inputData = "date,product_id,currency,price\n" +
                "20160101,1,EUR,10.0";
        // When
        standardEnricher.enrich(inputData);

        // Then
        verify(timeStampValidator, times(1)).validate(any());
    }

    @Test
    void executeValidatorMoreThanOnce() {
        // Given
        final String inputData = "date,product_id,currency,price\n" +
                "20160101,1,EUR,10.0\n20160101,1,EUR,10.0\n20160101,1,EUR,10.0\n";
        // When
        standardEnricher.enrich(inputData);

        // Then
        verify(timeStampValidator, times(3)).validate(any());
    }

    @Test
    void callProductServiceForId() {
        // Given
        final String inputData = "date,product_id,currency,price\n" +
                "20160101,1,EUR,10.0\n20160101,4,EUR,10.0\n20160101,1,EUR,10.0\n";
        // When
        standardEnricher.enrich(inputData);

        // Then
        verify(productService, times(1)).readProductName(4);
    }

    @Test
    void verifyIfEnricherAddProductIdToTrade() {
        // Given
        final String productName = "MY_PRODUCT_NAME";
        final String inputData = "date,product_id,currency,price\n" +
                "20160101,1,EUR,10.0\n20160101,4,EUR,10.0\n20160101,1,EUR,10.0\n";

        when(productService.readProductName(4)).thenReturn(productName);
        when(productService.readProductName(1)).thenReturn("1");
        // When
        List<String> result = standardEnricher.enrich(inputData);

        // Then
        verify(productService, times(1)).readProductName(4);
        result.get(1).matches("20160101,"+productName+",EUR,10.0");
    }

}
