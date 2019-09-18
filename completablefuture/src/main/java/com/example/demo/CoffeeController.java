package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CoffeeController {

    private final CoffeeComponent coffeeComponent;

    @GetMapping("/coffees/price")
    public int getPrice(@RequestParam(name = "name") String name){

        return coffeeComponent.getPriceAsync(name).join();
    }
}
