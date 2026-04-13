package ru.bsuedu.cad.lab.impls;

import java.io.IOException;
import java.util.Properties;

import org.springframework.stereotype.Component;


@Component("property")
public class Property {

    public String getProductInputFileName() {
        Properties prop = new Properties();
        try {
            prop.load(Property.class.getClassLoader().getResourceAsStream("application.properties"));
            return prop.getProperty("product.inputFileName");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getCategoryInputFileName() {
        Properties prop = new Properties();
        try {
            prop.load(Property.class.getClassLoader().getResourceAsStream("application.properties"));
            return prop.getProperty("category.inputFileName");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
