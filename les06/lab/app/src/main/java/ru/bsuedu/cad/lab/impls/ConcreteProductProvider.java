package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.intfs.Reader;
import ru.bsuedu.cad.lab.intfs.Parser;
import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.intfs.ProductProvider;


@Component
public class ConcreteProductProvider implements ProductProvider{
    private final Reader reader;
    private final Parser<Product> parser;
    
    
    @Autowired
    public ConcreteProductProvider(
            @Qualifier("productReader")Reader reader, 
            @Qualifier("productParser")Parser<Product> parser) {
        this.reader = reader;
        this.parser = parser;
    }

    @Override
    public ArrayList<Product> getProducts() {
        String resourceContent = reader.read();
        ArrayList<Product> products = parser.parse(resourceContent); 

        return products;
    }
}
