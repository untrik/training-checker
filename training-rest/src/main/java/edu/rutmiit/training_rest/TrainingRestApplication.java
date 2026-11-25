package edu.rutmiit.training_rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication(
        scanBasePackages = {"edu.rutmiit.training_rest", "edu.rutmiit.training_checker_api_contract", "edu.rutmiit.events"}
)
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class TrainingRestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TrainingRestApplication.class, args);
    }
}
