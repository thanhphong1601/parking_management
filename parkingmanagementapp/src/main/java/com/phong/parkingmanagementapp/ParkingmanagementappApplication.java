package com.phong.parkingmanagementapp;

import com.phong.parkingmanagementapp.configurations.DotenvApplicationContextInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class ParkingmanagementappApplication {

    public static void main(String[] args) {
//		SpringApplication.run(ParkingmanagementappApplication.class, args);
        new SpringApplicationBuilder(ParkingmanagementappApplication.class)
                .initializers(new DotenvApplicationContextInitializer()) // ép load ở đây
                .run(args);
    }
}
