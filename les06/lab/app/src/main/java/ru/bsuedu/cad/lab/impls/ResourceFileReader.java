package ru.bsuedu.cad.lab.impls;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.bsuedu.cad.lab.App;
import ru.bsuedu.cad.lab.intfs.Reader;
import ru.bsuedu.cad.lab.DataProcessingException;


public class ResourceFileReader implements Reader {

    private static final Logger logger = LoggerFactory.getLogger(CategoryRequest.class);
    
    private String inputFileName;

    
    public ResourceFileReader(String inputFileName) {
        this.inputFileName = inputFileName;
    }

    @PostConstruct
    public void init() {
        logger.info("\nResourceFileReader полностью инициализирован: " + "\n");
    }

    @Override
    public String read() {

        try (InputStream is = App.class.getResourceAsStream(inputFileName)) {
            if (is == null) {
                throw new DataProcessingException("Resource not found: " + inputFileName);
            }
            
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);            
        } catch (IOException e) {
            throw new DataProcessingException("Failed to read resource: " + inputFileName, e);
        }
    }
}