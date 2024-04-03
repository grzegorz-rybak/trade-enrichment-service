package com.verygoodbank.tes.validators;

import org.apache.commons.validator.GenericValidator;
import org.springframework.stereotype.Component;

@Component
public class TimeStampValidator implements TradeValidator {

    public static final String PATTERN = "yyyyMMdd";
    public static final int TIMESTAMP_POSITION = 0;

    @Override
    public void validate(final String[] trade) {
        boolean result = GenericValidator.isDate(trade[TIMESTAMP_POSITION], PATTERN, true);
        if (!result) {
            throw new IllegalArgumentException("Invalid date" + String.join(",", trade));
        }
    }

}
