package ru.bsuedu.cad.lab;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@Configuration
@ComponentScan({"ru.bsuedu.cad.lab.impls", "ru.bsuedu.cad.lab.aspects"})
@EnableAspectJAutoProxy
public class AppConfig {

}