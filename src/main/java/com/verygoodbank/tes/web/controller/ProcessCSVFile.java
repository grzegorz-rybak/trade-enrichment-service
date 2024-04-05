package com.verygoodbank.tes.web.controller;


import com.verygoodbank.tes.enricher.Enricher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessCSVFile implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProcessCSVFile.class);

    private final SseEmitter emitter;
    private final HttpServletRequest request;
    private final Enricher enricher;

    public ProcessCSVFile(final SseEmitter emitter, final HttpServletRequest request, final Enricher enricher) {
        this.emitter = emitter;
        this.request = request;
        this.enricher = enricher;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line = reader.readLine();
            LOGGER.debug("Header: {}", line);
            while ((line = reader.readLine()) != null) {
                emitter.send(enricher.enrichOne(line));
            }
            emitter.complete();
        } catch (final FileNotFoundException e) {
            LOGGER.error("Exception during input read; stream is not available");
            emitter.completeWithError(e);
        } catch (final IOException e) {
            LOGGER.error("Exception during input read for request: {}", request.getRequestId());
            emitter.completeWithError(e);
        }
    }

}
