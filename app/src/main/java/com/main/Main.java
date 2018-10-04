package com.main;

import api.CalcService;
import api.SayHelloService;

import java.io.IOException;
import java.util.*;


public class Main {

    public static void main(String... args) throws Exception {

     System.out.println("Modules in boot Layer:");
     ModuleLayer.boot().modules().stream()
             .filter((Module mod)-> !mod.getName().startsWith("java"))
             .filter((Module mod)-> !mod.getName().startsWith("jdk"))
             .forEach(System.out::println);

    List<Double> numbers = new ArrayList<>(Arrays.asList(55.5, 100.0, 44.0));
    double[] numberArray = {55.5, 100.0, 44.0};

    ServiceLoader<SayHelloService> helloServices = ServiceLoader.load(SayHelloService.class);
    ServiceLoader<CalcService> services = ServiceLoader.load(CalcService.class);
        if(!services.iterator().hasNext()){
            throw new RuntimeException("No Calc-Service providers found");
        }


        if(helloServices.iterator().hasNext()){
            SayHelloService s = helloServices.iterator().next();
           Optional<String> optString   = Optional.ofNullable(s.sayHello("Main App"));
            if(optString.isPresent())
                System.out.println( optString.get());
        }

        while (!Thread.currentThread().isInterrupted()){
            for(CalcService service:services){
                try {
                    long timestamp1 = System.nanoTime();
                    Optional<Double> d =  Optional.ofNullable(service.calc(numberArray));
                    long timestamp2 = System.nanoTime();
                    if(d.isPresent()){
                        System.out.println("result of calcService : " + d.get());
                        System.out.print("recived after " + (timestamp2 - timestamp1) + " nanoseconds" );
                        System.out.println("");
                    }else{
                        System.out.println("Service not reachable");
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());

                }

            }
            Thread.sleep(20000);

        }


    }
}
