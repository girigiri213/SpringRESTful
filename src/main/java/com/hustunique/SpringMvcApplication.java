package com.hustunique;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class SpringMvcApplication extends SpringBootServletInitializer {

    private static final String DISPATCHER_SERVLET_MAPPING = "/";

	public static void main(String[] args) {
		SpringApplication.run(SpringMvcApplication.class, args);
	}

}
