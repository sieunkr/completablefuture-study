package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        CoffeeComponent.class,
        CoffeeRepository.class})
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

        Integer expectedPrice = 1100;

        CompletableFuture<Void> future = coffeeComponent
                .getPriceAsync("latte")
                .thenAccept(p -> {
                    log.info("콜백, 가격은 " + p + "원, 하지만 데이터를 반환하지는 않음");
                    assertEquals(expectedPrice, p);
                });

        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능, 논블록킹");

        /*
        아래 구문이 없으면, main thread 가 종료되기 때문에, thenAccept 확인하기 전에 끝나버림.
        그래서, 테스트를 위해서 메인쓰레드가 종료되지 않도록 블록킹으로 대기하기 위한 코드
        future 가 complete 가 되면 위에 작성한 thenAccept 코드가 실행이 됨
        */
        assertNull(future.join());
    }


    @Test
    public void 가격_조회_비동기_호출_콜백_반환_테스트(){

        Integer expectedPrice = 1100 + 100;

        CompletableFuture<Void> future = coffeeComponent
                .getPriceAsync("latte")
                .thenApply(p -> {
                    log.info("같은 쓰레드로 동작");
                    return p + 100;
                })
                .thenAccept(p -> {
                    log.info("콜백, 가격은 " + p + "원, 하지만 데이터를 반환하지는 않음");
                    assertEquals(expectedPrice, p);
                });

        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능");

        /*
        아래 구문이 없으면, main thread 가 종료되기 때문에, thenAccept 확인하기 전에 끝나버림.
        그래서, 테스트를 위해서 메인쓰레드가 종료되지 않도록 블록킹으로 대기하기 위한 코드
        future 가 complete 가 되면 위에 작성한 thenAccept 코드가 실행이 됨
        */
        assertNull(future.join());
    }


    @Test
    public void 가격_조회_비동기_호출_콜백_다른쓰레드로_테스트(){

        Integer expectedPrice = 1100 + 100;
        Executor executor = Executors.newFixedThreadPool(5);

        CompletableFuture<Void> future = coffeeComponent
                .getPriceAsync("latte")
                .thenApplyAsync(p -> {
                    log.info("다른 쓰레드로 동작");
                    return p + 100;
                }, executor)
                .thenAcceptAsync(p -> {
                    log.info("콜백, 가격은 " + p + "원, 하지만 데이터를 반환하지는 않음");
                    assertEquals(expectedPrice, p);
                }, executor);

        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능");

        /*
        아래 구문이 없으면, main thread 가 종료되기 때문에, thenAccept 확인하기 전에 끝나버림.
        그래서, 테스트를 위해서 메인쓰레드가 종료되지 않도록 블록킹으로 대기하기 위한 코드
        future 가 complete 가 되면 위에 작성한 thenAccept 코드가 실행이 됨
        */
        assertNull(future.join());
    }


    @Test
    public void thenCombine_test(){

        Integer expectedPrice = 1100 + 1300;

        CompletableFuture<Integer> futureA = coffeeComponent.getPriceAsync("latte");
        CompletableFuture<Integer> futureB = coffeeComponent.getPriceAsync("mocha");

        //futureA.thenCombine(futureB, (a, b) -> a + b);
        Integer resultPrice = futureA.thenCombine(futureB, Integer::sum).join();

        assertEquals(expectedPrice, resultPrice);
    }


    @Test
    public void thenCompose_test(){

        Integer expectedPrice = (int)(1100 * 0.9);

        CompletableFuture<Integer> futureA = coffeeComponent.getPriceAsync("latte");

        Integer resultPrice = futureA.thenCompose(result ->
                coffeeComponent.getDiscountPriceAsync(result)).join();

        assertEquals(expectedPrice, resultPrice);
    }


    @Test
    public void allOf_test(){

        Integer expectedPrice = 1100 + 1300 + 900;

        CompletableFuture<Integer> futureA = coffeeComponent.getPriceAsync("latte");
        CompletableFuture<Integer> futureB = coffeeComponent.getPriceAsync("mocha");
        CompletableFuture<Integer> futureC = coffeeComponent.getPriceAsync("americano");

        List<CompletableFuture<Integer>> completableFutureList
                = Arrays.asList(futureA, futureB, futureC);

        //Integer resultPrice = CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[3]))
        Integer resultPrice = CompletableFuture.allOf(futureA, futureB, futureC)
                .thenApply(Void -> completableFutureList.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .join()
                .stream()
                .reduce(0, Integer::sum);

        assertEquals(expectedPrice, resultPrice);

    }
}