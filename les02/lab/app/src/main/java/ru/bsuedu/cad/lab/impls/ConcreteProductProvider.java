package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import ru.bsuedu.cad.lab.intfs.Reader;
import ru.bsuedu.cad.lab.intfs.Parser;
import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.intfs.ProductProvider;


public class ConcreteProductProvider implements ProductProvider{
    private final Reader reader;
    private final Parser parser;
    
    
    public ConcreteProductProvider(Reader reader, Parser parser) {
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
