package com.verygoodbank.tes.products;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.StringTokenizer;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class SimpleProductService implements ProductService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleProductService.class);
    public static final int INITIAL_MAP_CAPACITY = 10_000; //Up to 100k
    private static final String DEFAULT_PRODUCT_NAME = "Missing Product Name";
    public static final int PRODUCT_ID = 0;
    public static final int PRODUCT_NAME = 1;
    @Value("classpath:product.csv")
    private Resource resource;
    private HashMap<Long,String> names = new HashMap<>(INITIAL_MAP_CAPACITY);

    /**
     * Preprocessing
     * Invoked while a service bean is prepared.
     * */
    @PostConstruct
    public void initialize(){
        LOGGER.debug("Initialize Product service");

        String products = readProducts();
        StringTokenizer productTokenizer = new StringTokenizer(products, "\n");
        if(productTokenizer.hasMoreElements()){
            LOGGER.debug("Products file header: "+productTokenizer.nextElement());
        }
        while (productTokenizer.hasMoreElements()) {
            String[] trade  = productTokenizer.nextElement().toString().split(",");
            names.put(Long.valueOf(trade[PRODUCT_ID]),trade[PRODUCT_NAME]);
        }
    }

    @Override
    public String readProductName(final long id) {
        final String name = names.get(id);
        if(Objects.isNull(name)){
            LOGGER.warn("Product with id {} does not exist in super bank", id);
            return DEFAULT_PRODUCT_NAME;
        }
        return name;
    }

    private String readProducts() {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Only package access to limit its abuse
     * */
    HashMap<Long, String> getNames() {
        return names;
    }
}
