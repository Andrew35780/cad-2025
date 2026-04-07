package ru.bsuedu.cad.lab.impls;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import org.springframework.stereotype.Component;

@Component("property")
public class Property {

    public String getInputFileName() {
        Properties prop = new Properties();
        try {
            prop.load(Property.class.getClassLoader().getResourceAsStream("application.properties"));
            return prop.getProperty("inputFileName");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    // public Path getOutputFilePath() {
    //      Properties prop = new Properties();
    //     try {
    //         prop.load(Property.class.getClassLoader().getResourceAsStream("application.properties"));
    //         return Path.of(prop.getProperty("outputFilePath"));
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
    
}
