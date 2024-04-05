package com.verygoodbank.tes.products;


import com.google.common.cache.LoadingCache;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SimpleProductServiceTest {

    @Mock
    private LoadingCache cache;
    @InjectMocks
    private SimpleProductService simpleProductService;

    @Test
    void productSearchingTest() {
        // Given
        simpleProductService.setCache(cache);
        when(cache.getUnchecked(anyLong())).thenReturn("REPO Domestic");

        // When
        String result = simpleProductService.readProductName(3);

        // Then
        assertThat(result).contains("REPO Domestic");
    }

}
