package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ru.bsuedu.cad.lab.Category;
import ru.bsuedu.cad.lab.intfs.CategoryDao;


@Repository
public class CategoryDaoJdbcTemplate extends AbstractBatchDao<Category> implements CategoryDao{

    @Autowired
    public CategoryDaoJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    @Override
    public void saveAll(ArrayList<Category> categories) {
        String sql = """
                INSERT INTO CATEGORIES
                (category_id, name, description)
                VALUES
                (:categoryId, :name, :description)
                """;
        
        batchInsert(sql, categories);
    }
}