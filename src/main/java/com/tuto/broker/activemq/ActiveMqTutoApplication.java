package com.tuto.broker.activemq;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
public class ActiveMqTutoApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActiveMqTutoApplication.class, args);
	}

	@Bean
	@Qualifier("fakerExecutor")
	public Executor executor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(500);
		executor.setMaxPoolSize(1000);
		executor.setQueueCapacity(5000);
		executor.setThreadNamePrefix("faker-");
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.initialize();
		return executor;
	}

}
