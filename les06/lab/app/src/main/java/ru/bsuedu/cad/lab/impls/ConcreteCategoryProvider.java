package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Category;
import ru.bsuedu.cad.lab.intfs.*;;


@Component
public class ConcreteCategoryProvider implements CategoryProvider {
    private final Reader reader;
    private final Parser<Category> parser;
    
    
    @Autowired
    public ConcreteCategoryProvider(
            @Qualifier("categoryReader")Reader reader, 
            @Qualifier("categoryParser")Parser<Category> parser) {
        this.reader = reader;
        this.parser = parser;
    }

    @Override
    public ArrayList<Category> getCategories() {
        String resourceContent = reader.read();
        ArrayList<Category> categories = parser.parse(resourceContent); 

        return categories;
    }
}
