package com.example.demo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Coffee {
    private String name;
    private int price;
}