package com.example.msreservaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class MsReservacionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsReservacionesApplication.class, args);
    }

}
