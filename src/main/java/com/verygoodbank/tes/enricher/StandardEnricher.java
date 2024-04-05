package com.verygoodbank.tes.enricher;

import com.verygoodbank.tes.products.ProductService;
import com.verygoodbank.tes.products.SimpleProductService;
import com.verygoodbank.tes.validators.TradeValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class StandardEnricher implements Enricher {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleProductService.class);

    @Autowired
    private ProductService productService;

    @Autowired
    @Qualifier("timeStampValidator")
    private TradeValidator timeStampValidator;

    @Override
    public String enrichOne(final String oneTrade) {
        final String [] tradeVArray = oneTrade.split(",");
        timeStampValidator.validate(tradeVArray);
        tradeVArray[1] = productService.readProductName(Long.valueOf(tradeVArray[1]));
        return String.join(",", tradeVArray);
    }

    @Override
    public List<String> enrich(final String trades) {
        final List<String> output = new ArrayList<>();

        StringTokenizer tradesTokenizer = new StringTokenizer(trades, "\n");
        if (tradesTokenizer.hasMoreElements()) {
            LOGGER.debug("<HEADER>: " + tradesTokenizer.nextElement());
        }
        while (tradesTokenizer.hasMoreElements()) {
            try {
                String trade = tradesTokenizer.nextElement().toString();
                String enriched = enrichOne(trade);
                output.add(enriched + "\n");
            } catch (IllegalArgumentException e) {
                LOGGER.error("Row is corrupted", e);
            }
        }
        return output;
    }
}
