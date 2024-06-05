package org.example.cafeflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication()
public class CafeFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(CafeFlowApplication.class, args);
    }
}
