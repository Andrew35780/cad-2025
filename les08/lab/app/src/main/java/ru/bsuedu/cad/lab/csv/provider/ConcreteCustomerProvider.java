package ru.bsuedu.cad.lab.csv.provider;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.io.Reader;
import ru.bsuedu.cad.lab.csv.dto.CustomerCsvRow;
import ru.bsuedu.cad.lab.csv.parser.Parser;


@Component
public class ConcreteCustomerProvider implements CustomerProvider{
    private final Reader reader;
    private final Parser<CustomerCsvRow> parser;
    
    
    @Autowired
    public ConcreteCustomerProvider(
            @Qualifier("customerReader")Reader reader, 
            @Qualifier("customerParser")Parser<CustomerCsvRow> parser) {
        this.reader = reader;
        this.parser = parser;
    }

    @Override
    public ArrayList<CustomerCsvRow> getCustomers() {
        String resourceContent = reader.read();
        ArrayList<CustomerCsvRow> customers = parser.parse(resourceContent); 

        return customers;
    }
}
