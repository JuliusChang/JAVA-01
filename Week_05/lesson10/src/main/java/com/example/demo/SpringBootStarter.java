
package com.example.demo.SpringBootStarter;
import io.ISchool;
import io.School;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;



@Configuration
@ImportResource("classpath:/applicationContext.xml")
public class SpringBootStarter implements ApplicationContextAware {

    ApplicationContext applicationContext;

    @Bean
    public ISchool school() {
        return applicationContext.getAutowireCapableBeanFactory().createBean(School.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}