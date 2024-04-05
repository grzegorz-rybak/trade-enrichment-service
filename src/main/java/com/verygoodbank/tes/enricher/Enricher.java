package com.verygoodbank.tes.enricher;

import java.util.List;

public interface Enricher {


    String enrichOne(String oneTrade);

    List<String> enrich(String trades);

}
