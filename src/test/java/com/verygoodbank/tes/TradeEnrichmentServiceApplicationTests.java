package com.verygoodbank.tes;


import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TradeEnrichmentServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Value("classpath:trade.csv")
    private Resource resourceAsRequest;
    @Value("classpath:enrichedTrade.csv")
    private Resource resourceAsExpected;

    @Test
    void shouldReturnDefaultMessage() throws Exception {
        String request = IOUtils.toString(resourceAsRequest.getInputStream(), StandardCharsets.UTF_8.name());
        String expected = IOUtils.toString(resourceAsExpected.getInputStream(), StandardCharsets.UTF_8.name());

        this.mockMvc.perform(
                        post("/api/v1/enrich").content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(expected)));
    }

}

