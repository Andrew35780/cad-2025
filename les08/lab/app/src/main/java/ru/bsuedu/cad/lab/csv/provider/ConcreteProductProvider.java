package ru.bsuedu.cad.lab.csv.provider;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.io.Reader;
import ru.bsuedu.cad.lab.csv.dto.ProductCsvRow;
import ru.bsuedu.cad.lab.csv.parser.Parser;


@Component
public class ConcreteProductProvider implements ProductProvider{
    private final Reader reader;
    private final Parser<ProductCsvRow> parser;
    
    
    @Autowired
    public ConcreteProductProvider(
            @Qualifier("productReader")Reader reader, 
            @Qualifier("productParser")Parser<ProductCsvRow> parser) {
        this.reader = reader;
        this.parser = parser;
    }

    @Override
    public ArrayList<ProductCsvRow> getProducts() {
        String resourceContent = reader.read();
        ArrayList<ProductCsvRow> products = parser.parse(resourceContent); 

        return products;
    }
}
