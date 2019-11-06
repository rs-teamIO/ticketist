package com.siit.ticketist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TicketistApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketistApplication.class, args);
    }

}
