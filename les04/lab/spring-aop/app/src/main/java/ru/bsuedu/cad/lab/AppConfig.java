package ru.bsuedu.cad.lab;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.bsuedu.cad.lab.intfs.*;
import ru.bsuedu.cad.lab.impls.*;
import ru.bsuedu.cad.lab.aspects.*;;


@Configuration
@ComponentScan({"ru.bsuedu.cad.lab.impls", "ru.bsuedu.cad.lab.aspects"})
public class AppConfig implements BeanFactoryAware{

    private BeanFactory beanFactory;
    

    @Bean
    public Parser parser() {
        return new CSVParser();
    }

    @Bean
    public Advisor timingAdvisor() {
        return TimingAdvisor.createTimingAdvisor();
    }

    @Bean
    public Parser proParser() {
        var proxy = new ProxyFactoryBean();
        proxy.setInterceptorNames("timingAdvisor");
        proxy.setBeanFactory(beanFactory);
        proxy.setTarget(parser());
        return (Parser) proxy.getObject();
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}