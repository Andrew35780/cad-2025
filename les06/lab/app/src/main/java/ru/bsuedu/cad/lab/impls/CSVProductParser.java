package ru.bsuedu.cad.lab.impls;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Component;

import ru.bsuedu.cad.lab.Product;


@Component("productParser")
public class CSVProductParser extends AbstractCSVParser<Product> {

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
    protected Product mapRow(String[] cols, Map<String, Integer> headerIndex, int lineNumber) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

        long productId = parseLong(getValue(cols, headerIndex, "product_id"), lineNumber, "product_id");
        String name = getValue(cols, headerIndex, "name");
        String description = getValue(cols, headerIndex, "description");
        int categoryId = parseInt(getValue(cols, headerIndex, "category_id"), lineNumber, "category_id");
        BigDecimal price = parseBigDecimal(getValue(cols, headerIndex, "price"), lineNumber, "price");
        int stockQuantity = parseInt(getValue(cols, headerIndex, "stock_quantity"), lineNumber, "stock_quantity");
        String imageUrl = getValue(cols, headerIndex, "image_url");
        Date createdAt = parseDate(getValue(cols, headerIndex, "created_at"), formatter, lineNumber, "created_at");
        Date updatedAt = parseDate(getValue(cols, headerIndex, "updated_at"), formatter, lineNumber, "updated_at");

        return new Product(
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