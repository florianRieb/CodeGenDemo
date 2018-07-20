package com.main;

import api.CalcService;
import api.SayHelloService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;




public class Main {

    public static void main(String... args) throws Exception {




     ModuleLayer.boot().modules().forEach(System.out::println);
    List<Double> numbers = new ArrayList<>(Arrays.asList(55.5, 100.0, 44.0));
    double[] numberArray = {55.5, 100.0, 44.0};

    ServiceLoader<SayHelloService> helloServices = ServiceLoader.load(SayHelloService.class);
    ServiceLoader<CalcService> services = ServiceLoader.load(CalcService.class);
        if(!services.iterator().hasNext()){
            throw new RuntimeException("No Calc-Service providers found");
        }
        if(!helloServices.iterator().hasNext()){
            throw new RuntimeException("No Hello-Service providers found");
        }

        helloServices.iterator().next().sayHello("Main App");

        while (!Thread.currentThread().isInterrupted()){
            for(CalcService service:services){
                try {
                    System.out.println(service.toString());
                    System.out.println("result: " +service.calc(numberArray));

                } catch (IOException e) {
                    System.out.println(e.getMessage());

                }

            }
            Thread.sleep(20000);


        }



    }
}
