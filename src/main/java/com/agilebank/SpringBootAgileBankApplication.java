package com.agilebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Central SpringBoot runner class. Loads the Spring Context and scans for beans.
 */
@SpringBootApplication
public class SpringBootAgileBankApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBootAgileBankApplication.class, args);
  }
}
