package com.example.demo;

import java.util.concurrent.Future;

public interface CoffeeUseCase {
    int getPrice(String name);                              //Sync(동기)
    Future<Integer> getPriceAsync(String name);             //Async(비동기)
    Future<Integer> getDiscountPriceAsync(Integer price);   //Async(비동기)
}


