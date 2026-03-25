package ru.bsuedu.cad.lab.impls;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import ru.bsuedu.cad.lab.App;
import ru.bsuedu.cad.lab.intfs.Reader;
import ru.bsuedu.cad.lab.DataProcessingException;


public class ResourceFileReader implements Reader {

    private static final String RESOURCE_PATH = "/product.csv";


    @Override
    public String read() {

        try (InputStream is = App.class.getResourceAsStream(RESOURCE_PATH)) {
            if (is == null) {
                throw new DataProcessingException("Resource not found: " + RESOURCE_PATH);
            }
            
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);            
        } catch (IOException e) {
            throw new DataProcessingException("Failed to read resource: " + RESOURCE_PATH, e);
        }
    }
}