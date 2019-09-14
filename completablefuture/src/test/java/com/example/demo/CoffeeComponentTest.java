package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CoffeeComponent.class, CoffeeRepository.class})
public class CoffeeComponentTest {

    @Autowired
    private CoffeeComponent coffeeComponent;


    @Test
    public void 가격_조회_동기_블록킹_호출_테스트(){

        int expectedPrice = 1100;

        int resultPrice = coffeeComponent.getPrice("latte");
        log.info("최종 가격 전달 받음");

        assertEquals(expectedPrice, resultPrice);

    }


    @Test
    public void 가격_조회_비동기_블록킹_호출_테스트(){

        int expectedPrice = 1100;

        CompletableFuture<Integer> future = coffeeComponent.getPriceAsync("latte");
        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능");
        int resultPrice = future.join();  //블록킹
        log.info("최종 가격 전달 받음");

        assertEquals(expectedPrice, resultPrice);
    }


    @Test
    public void 가격_조회_비동기_호출_콜백_반환없음_테스트(){

        CompletableFuture<Void> future = coffeeComponent
                .getPriceAsync("latte")
                .thenAccept(p -> log.info("콜백, 가격은 " + p + "원, 하지만 데이터를 반환하지는 않음"));

        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능");

        assertNull(future.join());
    }


    @Test
    public void 가격_조회_비동기_호출_콜백_테스트(){

        int expectedPrice = 1100 + 100;

        CompletableFuture<Integer> future = coffeeComponent
                .getPriceAsync("latte")
                .thenApply(p -> {
                    log.info("같은 쓰레드로 동작");
                    return p + 100;
                });

        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능");

        int resultPrice = future.join();  //블록킹
        log.info("최종 가격 전달 받음");

        assertEquals(expectedPrice, resultPrice);
    }

    @Test
    public void 가격_조회_비동기_호출_콜백_다른쓰레드로_테스트(){

        int expectedPrice = 1100 + 100;
        Executor executor = Executors.newFixedThreadPool(5);

        CompletableFuture<Integer> future = coffeeComponent
                .getPriceAsync("latte")
                .thenApplyAsync(p -> {
                    log.info("다른 쓰레드로 동작");
                    return p + 100;
                }, executor);

        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능");

        int resultPrice = future.join();  //블록킹
        log.info("최종 가격 전달 받음");

        assertEquals(expectedPrice, resultPrice);
    }

    

}