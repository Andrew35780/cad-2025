package ru.bsuedu.cad.lab.impls;

import java.util.ArrayList;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;


public abstract class AbstractBatchDao<T> {
    protected final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    protected AbstractBatchDao(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    protected void batchInsert(String sql, ArrayList<T> items) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(items.toArray());
        namedParameterJdbcTemplate.batchUpdate(sql, batch);
    }
}