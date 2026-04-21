package ru.bsuedu.cad.lab.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.zaxxer.hikari.HikariDataSource;

import ru.bsuedu.cad.lab.io.ResourceFileReader;
import ru.bsuedu.cad.lab.io.Reader;

@Configuration
@ComponentScan("ru.bsuedu.cad.lab")
@PropertySource("classpath:jdbc.properties")
public class AppConfigBasic {

    @Value("${jdbc.driverClassName}")
    private String driverClassName;
    @Value("${jdbc.url}")
    private String url;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.password}")
    private String password;
    @Value("${jdbc.maxPoolSize}")
    private Integer maxPoolSize;

   
    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariDataSource hDataSource = new HikariDataSource();
        hDataSource.setJdbcUrl(url);
        hDataSource.setUsername(username);
        hDataSource.setPassword(password);
        hDataSource.setDriverClassName(driverClassName);
        hDataSource.setMaximumPoolSize(maxPoolSize);

        return hDataSource;
    }

    @Bean("productReader")
    public Reader productReader(@Value("#{property.productInputFileName}") String fileName) {
        return new ResourceFileReader(fileName);
    }

    @Bean("categoryReader")
    public Reader categoryReader(@Value("#{property.categoryInputFileName}") String fileName) {
        return new ResourceFileReader(fileName);
    }

    @Bean("customerReader")
    public Reader customerReader(@Value("#{property.customerInputFileName}") String fileName) {
        return new ResourceFileReader(fileName);
    }
}