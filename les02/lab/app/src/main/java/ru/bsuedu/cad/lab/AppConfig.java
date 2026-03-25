package ru.bsuedu.cad.lab;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.bsuedu.cad.lab.intfs.*;
import ru.bsuedu.cad.lab.impls.*;


@Configuration
public class AppConfig {

    @Bean
    public Reader reader() {
        return new ResourceFileReader();
    }

    @Bean
    public Parser parser() {
        return new CSVParser();
    }

    @Bean
    public ProductProvider productProvider(Reader reader, Parser parser) {
        return new ConcreteProductProvider(reader, parser); //
    }

    @Bean
    public Renderer renderer(ProductProvider provider) {
        return new ConsoleTableRenderer(provider);
    }
}