package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Repository
public class CategoryRequest {
    private static final Logger logger = LoggerFactory.getLogger(CategoryRequest.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CategoryRequest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public void execute() {

        String sql = """
            SELECT c.category_id,
                   c.name,
                   c.description,
            COUNT(p.product_id) AS product_count
            FROM categories c
            JOIN products p ON p.category_id = c.category_id
            GROUP BY c.category_id, c.name, c.description
            HAVING COUNT(p.product_id) > 1
            ORDER BY c.category_id
                    """;
        
        ArrayList<CategoryStat> categoriesStat = new ArrayList<>(jdbcTemplate.query(sql, categoryStatRowMapper()));

        if (categoriesStat.isEmpty()) {
            logger.info("Категории, в которых больше одного товара - отсутствуют");
            return;
        }

        for (var category : categoriesStat) {
            logger.info("category_id = {}, name = {}, description = {}, product_count = {} \n\n", 
            category.categoryId(), category.name(), category.description(), category.productCount());
        }

    }

    private RowMapper<CategoryStat> categoryStatRowMapper() {
        return (rs, rowNum) -> new CategoryStat(
                rs.getLong("category_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getInt("product_count"));
    }

    public record CategoryStat(long categoryId, String name, String description, int productCount) {}
}
