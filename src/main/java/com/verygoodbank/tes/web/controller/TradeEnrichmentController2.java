package com.verygoodbank.tes.web.controller;

import com.verygoodbank.tes.enricher.Enricher;
import com.verygoodbank.tes.products.SimpleProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("api/v2")
public class TradeEnrichmentController2 {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleProductService.class);

    @Autowired
    private Enricher enricher;

    @Autowired
    private TaskExecutor taskExecutor;

    @RequestMapping(value = "/enrich", method = RequestMethod.POST)
    public SseEmitter enrichTrades(final HttpServletRequest request) {
        LOGGER.debug("Start to process a new request for {}", request.getRequestId());
        final SseEmitter emitter = new SseEmitter(60_000L);
        taskExecutor.execute(new ProcessCSVFile(emitter, request, enricher));
        LOGGER.debug("Request added to executor {}", request.getRequestId());
        return emitter;
    }

}


