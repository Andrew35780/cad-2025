package ru.bsuedu.cad.lab.csv.parser;

import java.util.Map;

import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.csv.dto.CategoryCsvRow;


@Component("categoryParser")
public class CategoryCsvParser extends AbstractCsvParser<CategoryCsvRow> {

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
    protected CategoryCsvRow mapRow(String[] cols, Map<String, Integer> headerIndex, int lineNumber) {
        Long categoryId = parseLong(getValue(cols, headerIndex, "category_id"), lineNumber, "category_id");
        String name = getValue(cols, headerIndex, "name");
        String description = getValue(cols, headerIndex, "description");

        return new CategoryCsvRow(categoryId, name, description);
    }
}