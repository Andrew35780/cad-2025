package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.bsuedu.cad.lab.Product;
import ru.bsuedu.cad.lab.intfs.ProductDao;


@Repository
public class ProductDaoJdbcTemplate extends AbstractBatchDao<Product> implements ProductDao {

    @Autowired
    public ProductDaoJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    @Override
    public void saveAll(ArrayList<Product> products) {
        String sql = """
                INSERT INTO PRODUCTS
                (product_id, name, description, category_id, price, stock_quantity, image_url, created_at, updated_at)
                VALUES
                (:productId, :name, :description, :categoryId, :price, :stockQuantity, :imageUrl, :createdAt, :updatedAt)
                """;

        batchInsert(sql, products);

        // Manual realization
        /*
         * String sql = "INSERT INTO PRODUCTS" +
         * "(product_id, name, description, category_id, price, stock_quantity, image_url, created_at, updated_at)"
         * +
         * "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
         * 
         * for (Product product : products) {
         * jdbcTemplate.update(sql,
         * product.getProductId(),
         * product.getName(),
         * product.getDescription(),
         * product.getCategoryId(),
         * product.getPrice(),
         * product.getStockQuantity(),
         * product.getImageUrl(),
         * product.getCreatedAt(),
         * product.getUpdatedAt()
         * );
         * }
         */
    }
}
