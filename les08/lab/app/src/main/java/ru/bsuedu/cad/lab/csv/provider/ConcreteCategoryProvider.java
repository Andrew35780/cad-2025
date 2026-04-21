package ru.bsuedu.cad.lab.csv.provider;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.csv.dto.CategoryCsvRow;
import ru.bsuedu.cad.lab.csv.parser.Parser;
import ru.bsuedu.cad.lab.io.Reader;


@Component
public class ConcreteCategoryProvider implements CategoryProvider {
    private final Reader reader;
    private final Parser<CategoryCsvRow> parser;
    
    
    @Autowired
    public ConcreteCategoryProvider(
            @Qualifier("categoryReader")Reader reader, 
            @Qualifier("categoryParser")Parser<CategoryCsvRow> parser) {
        this.reader = reader;
        this.parser = parser;
    }

    @Override
    public ArrayList<CategoryCsvRow> getCategories() {
        String resourceContent = reader.read();
        ArrayList<CategoryCsvRow> categories = parser.parse(resourceContent); 

        return categories;
    }
}
