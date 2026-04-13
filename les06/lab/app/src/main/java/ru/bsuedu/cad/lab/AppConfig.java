package ru.bsuedu.cad.lab;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import ru.bsuedu.cad.lab.impls.ResourceFileReader;
import ru.bsuedu.cad.lab.intfs.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


@Configuration
@ComponentScan({ "ru.bsuedu.cad.lab.impls", "ru.bsuedu.cad.lab.aspects" })
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        var dbBuilder = new EmbeddedDatabaseBuilder();
        return dbBuilder.setType(EmbeddedDatabaseType.H2)
                .setName("market.db")
                .addScript("classpath:db/schema.sql")
                // .addScript("classpath:db/data.sql")
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean("productReader")
    public Reader productReader(@Value("#{property.productInputFileName}") String fileName) {
        return new ResourceFileReader(fileName);
    }

    @Bean("categoryReader")
    public Reader categoryReader(@Value("#{property.categoryInputFileName}") String fileName) {
        return new ResourceFileReader(fileName);
    }
}