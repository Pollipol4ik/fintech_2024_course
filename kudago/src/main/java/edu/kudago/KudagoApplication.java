package edu.kudago;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"edu.cbr", "edu.kudago"})
public class KudagoApplication {

    public static void main(String[] args) {
        SpringApplication.run(KudagoApplication.class, args);
    }

}
