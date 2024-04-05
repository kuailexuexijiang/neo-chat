package com.neo;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Configurable
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
