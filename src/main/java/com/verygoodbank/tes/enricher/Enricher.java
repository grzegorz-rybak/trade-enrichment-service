package com.verygoodbank.tes.enricher;

import java.util.List;

public interface Enricher {

    List<String> enrich(String trades);

}
