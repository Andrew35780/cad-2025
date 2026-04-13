package ru.bsuedu.cad.lab.impls;

import java.util.Map;

import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Category;


@Component("categoryParser")
public class CSVCategoryParser extends AbstractCSVParser<Category> {

    private static final String[] REQUIRED_COLUMNS = {
            "category_id",
            "name",
            "description"
    };

    @Override
    protected String[] requiredColumns() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected Category mapRow(String[] cols, Map<String, Integer> headerIndex, int lineNumber) {
        int categoryId = parseInt(getValue(cols, headerIndex, "category_id"), lineNumber, "category_id");
        String name = getValue(cols, headerIndex, "name");
        String description = getValue(cols, headerIndex, "description");

        return new Category(categoryId, name, description);
    }
}