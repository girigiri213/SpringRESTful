package com.girigiri.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Created by JianGuo on 6/26/16.
 * Configuration for RESTful validation
 */
@Configuration
public class RestValidationConfiguration extends RepositoryRestConfigurerAdapter {

    /**
     * Create a validator to use in bean validation - primary to be able to autowire without qualifier
     */
    @Bean
    @Primary
    Validator validator() {
        return new LocalValidatorFactoryBean();
    }


    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener validatingListener) {
        Validator validator = validator();
        // bean validation always before save and create
        validatingListener.addValidator("beforeCreate", validator);
        validatingListener.addValidator("beforeSave", validator);
    }
}
