package com.verygoodbank.tes.web.controller;

import com.verygoodbank.tes.products.ProductService;
import com.verygoodbank.tes.products.SimpleProductService;
import com.verygoodbank.tes.validators.TradeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@RestController
@RequestMapping("api/v1")
public class TradeEnrichmentController {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleProductService.class);

    @Autowired
    private ProductService productService;
    @Autowired
    @Qualifier("timeStampValidator")
    private TradeValidator timeStampValidator;

    @RequestMapping(value = "/enrich", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> enrichTrades(@RequestBody String trades) {
        final List<String> output  = new ArrayList<>();
        StringTokenizer tradesTokenizer = new StringTokenizer(trades, "\n");
        if(tradesTokenizer.hasMoreElements()){
            LOGGER.debug("<HEADER>: " + tradesTokenizer.nextElement());
        }
        while (tradesTokenizer.hasMoreElements()) {
            try {
                String[] trade = tradesTokenizer.nextElement().toString().split(",");
                timeStampValidator.validate(trade);
                trade[1] = productService.readProductName(Long.valueOf(trade[1]));
                output.add(String.join(",", trade) + "\n");
            }catch (IllegalArgumentException e){
                LOGGER.error("Row is corrupted", e);
            }
        }

        StreamingResponseBody stream =  is -> {
            is.write("date,product_id,currency,price\n".getBytes());
            output.forEach(enrichedTrade-> {
                try {
                    is.write(enrichedTrade.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        };
        return ResponseEntity.ok().body(stream);
    }




}


