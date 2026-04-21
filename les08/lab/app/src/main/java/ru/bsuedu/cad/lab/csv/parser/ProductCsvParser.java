package ru.bsuedu.cad.lab.csv.parser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.csv.dto.ProductCsvRow;


@Component("productParser")
public class ProductCsvParser extends AbstractCsvParser<ProductCsvRow> {

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
    protected String[] requiredColumns() {
        return REQUIRED_COLUMNS;
    }

    @Override
    protected ProductCsvRow mapRow(String[] cols, Map<String, Integer> headerIndex, int lineNumber) {
        Long productId = parseLong(getValue(cols, headerIndex, "product_id"), lineNumber, "product_id");
        String name = getValue(cols, headerIndex, "name");
        String description = getValue(cols, headerIndex, "description");
        Long categoryId = parseLong(getValue(cols, headerIndex, "category_id"), lineNumber, "category_id");
        BigDecimal price = parseBigDecimal(getValue(cols, headerIndex, "price"), lineNumber, "price");
        Integer stockQuantity = parseInt(getValue(cols, headerIndex, "stock_quantity"), lineNumber, "stock_quantity");
        String imageUrl = getValue(cols, headerIndex, "image_url");
        LocalDate createdAt = parseDate(getValue(cols, headerIndex, "created_at"), lineNumber, "created_at");
        LocalDate updatedAt = parseDate(getValue(cols, headerIndex, "updated_at"), lineNumber, "updated_at");

        return new ProductCsvRow(
                productId,
                name,
                description,
                categoryId,
                price,
                stockQuantity,
                imageUrl,
                createdAt,
                updatedAt);
    }
}