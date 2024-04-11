package jav.fenix.reactor;

import reactor.core.publisher.Flux;

/**
 * @author LiuFeng
 * @desc 描述
 * @date 2023/6/25 15:07
 * @since v1
 */
public class ReactorTest {


    public static void main(String[] args) {
//        Flux<Integer> integerFlux = Flux.just(1, 2, 3, 4, 5);
//        integerFlux.subscribe(System.out::println);
//
//        Flux<String> stringFlux = Flux.just("hello", "world");
//        stringFlux.subscribe(System.out::println);
//
//        Integer[] integers = {1, 2, 3, 4, 5};
//        Flux.fromArray(integers).subscribe(System.out::println);
//
//        List<Integer> list = Arrays.asList(integers);
//
//        Flux.fromIterable(list).subscribe(System.out::println);
//
//        Flux.fromStream(list.stream()).subscribe(System.out::println);

        Flux<Object> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next(state);
                    if (state == 5) {
                        sink.complete();
                    }
                    return ++state;
                }).log();
        flux.subscribe(System.out::println);

    }

}
