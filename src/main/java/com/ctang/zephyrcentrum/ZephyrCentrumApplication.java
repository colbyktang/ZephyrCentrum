package com.ctang.zephyrcentrum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.ctang.zephyrcentrum.repositories")
@EntityScan(basePackages = {"com.ctang.zephyrcentrum.models"})
public class ZephyrCentrumApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZephyrCentrumApplication.class, args);
	}

}
