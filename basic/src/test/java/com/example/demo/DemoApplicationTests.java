package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class DemoApplicationTests {

    /*
    스터디로 잠시 작성한 소스, 다시 코드 정리해야 함
    */


    
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
            return "ok-2";
        }).thenAccept(s -> System.out.println("[" + Thread.currentThread().getName() + "]" +   s));
    }


    @Test
    public void thenCompose(){
        //순차적으로 실행, 병렬실행 아님, 하나의 쓰레드 풀

        CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "]" +   "supplyAsync");
            return "supplyAsync";
        }).thenCompose(s -> CompletableFuture.completedFuture(s +"2"))
                .thenAccept(s -> System.out.println("[" + Thread.currentThread().getName() + "]complete=" +   s));

    }

    @Test
    public void thenCombine(){
        //병렬실행, 별도 쓰레드 풀

        CompletableFuture secondCompletableFuture = CompletableFuture.supplyAsync(() -> {

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("[" + Thread.currentThread().getName() + "]" +   "supplyAsync - 2");
            return "secondJob";
        });

        CompletableFuture.supplyAsync(() -> {

            System.out.println("[" + Thread.currentThread().getName() + "]" +   "supplyAsync - 1");
            return "firstJob";
        }).thenCombine(secondCompletableFuture, (s1, s2) -> s1 + " and " + s2)
                .thenAccept(s -> System.out.println("[" + Thread.currentThread().getName() + "]complete=" +   s));

    }


    @Test
    public void allOf() {

        CompletableFuture completableFuture1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "]" +   "future - 1");
            return "1";
        });

        CompletableFuture completableFuture2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "]" +   "future - 2");
            return "2";
        });

        CompletableFuture completableFuture3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "]" +   "future - 3");
            return "3";
        });

        List<CompletableFuture> futures = Arrays.asList(completableFuture1, completableFuture2, completableFuture3);

        CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3)
                .thenAccept(s -> {
                    List<Object> result = futures.stream()
                            //.map(pageContentFuture -> pageContentFuture.join())
                            .map(CompletableFuture::join)
                            .collect(Collectors.toList());

                    System.out.println("[" + Thread.currentThread().getName() + "]" +   result.toString());

                });



    }




}
