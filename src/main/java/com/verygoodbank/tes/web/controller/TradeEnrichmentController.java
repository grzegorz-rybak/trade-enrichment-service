package com.verygoodbank.tes.web.controller;

import com.verygoodbank.tes.enricher.Enricher;
import com.verygoodbank.tes.products.SimpleProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.List;


@RestController
@RequestMapping("api/v1")
public class TradeEnrichmentController {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleProductService.class);

    @Autowired
    private Enricher enricher;


    @RequestMapping(value = "/enrich", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> enrichTrades(@RequestBody String trades) {
        final List<String> enrichedTrades = enricher.enrich(trades);
        return ResponseEntity.ok().body(getStreamingResponseBody(enrichedTrades));
    }

    private static StreamingResponseBody getStreamingResponseBody(final List<String> output) {
        StreamingResponseBody stream = is -> {
            is.write("date,product_id,currency,price\n".getBytes());
            output.forEach(enrichedTrade -> {
                try {
                    is.write(enrichedTrade.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        };
        return stream;
    }


}


