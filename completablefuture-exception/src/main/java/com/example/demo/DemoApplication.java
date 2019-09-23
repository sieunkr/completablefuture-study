package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CompletableFuture;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
                throw new RuntimeException();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "";
        })
        .exceptionally(throwable -> "alternative");

        future.thenAccept(System.out::println);
        System.out.println("논블록킹");
    }
}
