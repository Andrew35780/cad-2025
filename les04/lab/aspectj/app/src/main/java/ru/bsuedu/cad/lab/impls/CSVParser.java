package ru.bsuedu.cad.lab.impls;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.intfs.Parser;
import ru.bsuedu.cad.lab.intfs.ThrowingParser;
import ru.bsuedu.cad.lab.DataProcessingException;


@Component
public class CSVParser implements Parser {

    private static final String[] REQUIRED_COLUMNS = {
            "product_id",
            "name",
            "description",
            "category_id",
            "price",
            "stock_quantity",
            "image_url",
            "created_at",
            "updated_at"
    };
    

    @Override
    public ArrayList<Product> parse(String text) {
        if (text == null)
            throw new DataProcessingException("CSV text is null");

        ArrayList<Product> products = new ArrayList<>();
        if (text.isBlank()) {
            return products;
        }

        String[] lines = text.split("\\R");
        if (lines.length == 0)
            return products;

        String[] headerCols = lines[0].trim().split(",", -1);
        Map<String, Integer> headerIndex = buildHeaderIndex(headerCols);

        validateRequiredColumns(headerIndex);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] cols = line.split(",", -1);
            int lineNumber = i + 1;

            long productId = parseLong(getValue(cols, headerIndex, "product_id"), lineNumber, "product_id");
            String name = getValue(cols, headerIndex, "name");
            String description = getValue(cols, headerIndex, "description");
            int categoryId = parseInt(getValue(cols, headerIndex, "category_id"), lineNumber, "category_id");
            BigDecimal price = parseBigDecimal(getValue(cols, headerIndex, "price"), lineNumber, "price");
            int stockQuantity = parseInt(getValue(cols, headerIndex, "stock_quantity"), lineNumber, "stock_quantity");
            String imageUrl = getValue(cols, headerIndex, "image_url");
            Date createdAt = parseDate(getValue(cols, headerIndex, "created_at"), formatter, lineNumber, "created_at");
            Date updatedAt = parseDate(getValue(cols, headerIndex, "updated_at"), formatter, lineNumber, "updated_at");

            products.add(new Product(
                    productId,
                    name,
                    description,
                    categoryId,
                    price,
                    stockQuantity,
                    imageUrl,
                    createdAt,
                    updatedAt));
        }

        return products;
    }
    

    private Map<String, Integer> buildHeaderIndex(String[] headerCols) {
        Map<String, Integer> index = new HashMap<>();
        for (int i = 0; i < headerCols.length; i++) {
            index.put(headerCols[i].trim().toLowerCase(Locale.ROOT), i);
        }
        return index;
    }

    private void validateRequiredColumns(Map<String, Integer> headerIndex) {
        for (String col : REQUIRED_COLUMNS) {
            if (!headerIndex.containsKey(col)) {
                throw new DataProcessingException("CSV header missing required column: " + col);
            }
        }
    }

    private String getValue(String[] cols, Map<String, Integer> headerIndex, String columnName) {
        Integer idx = headerIndex.get(columnName);
        if (idx == null || idx < 0 || idx >= cols.length) {
            return "";
        }
        return cols[idx].trim();
    }

    private <T> T parseValue(
            String value,
            int lineNumber,
            String columnName,
            String typeName,
            ThrowingParser<T> parser) {
        if (value == null || value.isBlank()) {
            throw new DataProcessingException(
                    "Empty " + typeName + " value in column '" + columnName + "' at line " + lineNumber);
        }

        try {
            return parser.parse(value);
        } catch (Exception e) {
            throw new DataProcessingException(
                    "Invalid " + typeName + " in column '" + columnName + "' at line " + lineNumber + ": " + value,
                    e);
        }
    }

    private int parseInt(String value, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "integer", Integer::parseInt);
    }

    private long parseLong(String value, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "long", Long::parseLong);
    }

    private BigDecimal parseBigDecimal(String value, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "decimal", BigDecimal::new);
    }

    private Date parseDate(String value, SimpleDateFormat formatter, int lineNumber, String columnName) {
        return parseValue(value, lineNumber, columnName, "date", formatter::parse);
    }
}

