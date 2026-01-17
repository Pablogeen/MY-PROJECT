package com.rey.me;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.rey.me.repository")
@EnableCaching
public class MeApplication {

	public static void main(String[] args) { 
		SpringApplication.run(MeApplication.class, args);
	}
}
