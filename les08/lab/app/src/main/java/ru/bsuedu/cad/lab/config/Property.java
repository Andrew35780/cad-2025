package ru.bsuedu.cad.lab.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component("property")
public class Property {

    private static final Properties PROPERTIES = loadProperties();
    private static final String RESOURCE_NAME = "application.properties";

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream stream = Property.class.getClassLoader().getResourceAsStream(RESOURCE_NAME)) {
            if (stream != null) {
                props.load(stream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    public String getProductInputFileName() {
        return PROPERTIES.getProperty("product.inputFileName", "");
    }

    public String getCategoryInputFileName() {
        return PROPERTIES.getProperty("category.inputFileName", "");
    }

    public String getCustomerInputFileName() {
        return PROPERTIES.getProperty("customer.inputFileName", "");
    }
}
