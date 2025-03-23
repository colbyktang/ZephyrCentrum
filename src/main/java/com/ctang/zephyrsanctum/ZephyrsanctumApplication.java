package com.ctang.zephyrsanctum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ctang.zephyrsanctum.repositories")
@EntityScan(basePackages = {"com.ctang.zephyrsanctum.models"})
public class ZephyrsanctumApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZephyrsanctumApplication.class, args);
	}

}
