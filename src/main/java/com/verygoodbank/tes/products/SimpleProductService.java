package com.verygoodbank.tes.products;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class SimpleProductService implements ProductService {

    private final static Logger LOGGER = LoggerFactory.getLogger(SimpleProductService.class);
    private static final String DEFAULT_PRODUCT_NAME = "Missing Product Name";
    public static final String INDEX_TEMP_FILE = "index_temp_file";
    @Value("classpath:product.csv")
    private Resource resource;

    private CacheLoader<Long, String> loader;
    private LoadingCache<Long, String> cache;

    private RandomAccessFile rafProducts = null;
    private RandomAccessFile rafIndex = null;

    private Long fileLengthProduct = 0L;
    private Long fileLengthIndex = 0L;
    /**
     * Preprocessing
     * Invoked while a service bean is prepared.
     * <p>
     * Runs through product file. creates an index file with product offsets.
     * It allows to RAF data acquisition for cache purposes.
     */
    @PostConstruct
    public void initialize() {
        LOGGER.debug("Initialize Product service");

        File indexFile = removeIndexFileAndGetPointer();

        try {
            rafProducts = new RandomAccessFile(resource.getFile(), "r");
            rafIndex = new RandomAccessFile(indexFile, "rw");
            fileLengthProduct = rafProducts.length();
            rafProducts.readLine();
            long pos = rafProducts.getFilePointer();
            long index = 0;
            while (pos < fileLengthProduct) {
                pos = rafProducts.getFilePointer();
                rafProducts.readLine();
                rafIndex.seek(index * Long.BYTES);
                rafIndex.writeLong(pos);
                index++;
            }
            fileLengthIndex = rafIndex.length();
        } catch (IOException e) {
            LOGGER.debug("There is an exception while creating index", e);
        }

        loader = new CacheLoader<Long, String>() {
            @Override
            public String load(Long key) {
                try {
                    final long rafIndexPositionToCheck = (key - 1) * Long.BYTES;
                    if(rafIndexPositionToCheck<0 || rafIndexPositionToCheck>=(fileLengthIndex-Long.BYTES)){
                        return null; // ignore invalid id as function parameter
                    }
                    rafIndex.seek(rafIndexPositionToCheck);
                    final long position = rafIndex.readLong();
                    if(position<0 || position>=fileLengthProduct){
                        throw new Error("Index failure - this shouldn't happen");
                    }
                    rafProducts.seek(position);
                    final String line = rafProducts.readLine();
                    return line.split(",")[1];
                } catch (IOException e) {
                    throw new Error("Close the app - this shouldn't happen");
                }
            }
        };

        cache = CacheBuilder.newBuilder().maximumSize(6).build(loader);

    }


    @PreDestroy
    public void closeConnections() {
        LOGGER.debug("Product service - graceful shutdown");
        try {
            rafIndex.close();
        } catch (IOException e) {
            LOGGER.error("Pointer to index file closing error", e);
            throw new RuntimeException(e);
        }
        try {
            rafProducts.close();
        } catch (IOException e) {
            LOGGER.error("Pointer to product file closing error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String readProductName(final long id) {
        try {
            return cache.getUnchecked(Long.valueOf(id));
        } catch (final CacheLoader.InvalidCacheLoadException e) {
            LOGGER.warn("Product with id {} does not exist in super bank", id);
            return DEFAULT_PRODUCT_NAME;
        }
    }

    private static File removeIndexFileAndGetPointer() {
        final File indexFile = new File(INDEX_TEMP_FILE);
        try {
            FileUtils.forceDelete(indexFile);
        } catch (IOException e) {
            LOGGER.error("There is no index file yet: {}", INDEX_TEMP_FILE);
        }
        return indexFile;
    }


    public void setCache(LoadingCache<Long, String> cache) {
        this.cache = cache;
    }
}
