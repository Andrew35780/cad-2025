package ru.bsuedu.cad.lab.impls;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.App;
import ru.bsuedu.cad.lab.intfs.Reader;
import ru.bsuedu.cad.lab.DataProcessingException;

@Component
public class ResourceFileReader implements Reader {

    @Value("#{property.inputFileName}")
    private String inputFileName;

    @PostConstruct
    public void init() {
        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        System.out.println("ResourceFileReader полностью инициализирован: " + now + "\n");
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