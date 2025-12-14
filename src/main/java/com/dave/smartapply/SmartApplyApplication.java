package com.dave.smartapply;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.dave.smartapply.repository")
@EntityScan("com.dave.smartapply.model")
public class SmartApplyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartApplyApplication.class, args);
	}

}
