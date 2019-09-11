package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class DemoApplicationTests {

    @Test
    public void static_runAsync(){
        try {
            //runAsync : Runnable Functional Interface, 파라미터 없음, 반환값 없음
            CompletableFuture.runAsync(() -> {
                System.out.println("[" + Thread.currentThread().getName() + "]" +   "runAsync");
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void static_supplyAsync(){

        try {

            //supplyAsync : Supplier Functional Interface, 파라미터 없음, 반환 있음
            String result =
                CompletableFuture.supplyAsync(() -> {
                    System.out.println("[" + Thread.currentThread().getName() + "]" +   "supplyAsync");
                    return "ok";
                }).get();

            System.out.println("[" + Thread.currentThread().getName() + "]" + "result = " + result);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void thenAccept(){

        //supplyAsync : Supplier Functional Interface, 파라미터 X, 반환 O
        CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "]" +   "supplyAsync");
            return "ok";
        }).thenAccept(s ->
            {
            //thenAccept : Consumer Functional Interface, 파라미터 O, 리턴 X
            System.out.println("[" + Thread.currentThread().getName() + "]" +   s);
        });

        //runAsync : Runnable Functional Interface, 파라미터 X, 반환 X
        CompletableFuture.runAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "]" +   "runAsync");
        }).thenAccept(s -> System.out.println("[" + Thread.currentThread().getName() + "]" +   s));

    }


    @Test
    public void thenApply(){

        //supplyAsync : Supplier Functional Interface, 파라미터 X, 반환 O
        CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "]" +   "supplyAsync");
            return "ok";
        }).thenApply(s ->
        {
            //thenAccept : Function Functional Interface, 파라미터 O, 리턴 O
            System.out.println("[" + Thread.currentThread().getName() + "]" +   s);
            return "ok2";
        }).thenAccept(s -> System.out.println("[" + Thread.currentThread().getName() + "]" +   s));

    }




}
