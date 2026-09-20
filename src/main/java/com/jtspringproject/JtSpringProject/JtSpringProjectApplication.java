package com.jtspringproject.JtSpringProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class JtSpringProjectApplication {
	private static final Logger logger = LoggerFactory.getLogger(JtSpringProjectApplication.class);

	public static void main(String[] args) {
		logger.info("Starting JtSpringProject application");
		SpringApplication.run(JtSpringProjectApplication.class, args);
	}

}
