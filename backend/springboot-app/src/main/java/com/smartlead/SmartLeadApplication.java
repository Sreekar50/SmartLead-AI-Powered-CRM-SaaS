// src/main/java/com/smartlead/SmartLeadApplication.java
package com.smartlead;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SmartLeadApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartLeadApplication.class, args);
    }
}