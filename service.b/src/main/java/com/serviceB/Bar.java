package com.serviceB;


import com.helper.ZMQServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Bar {
    public static void main(String... args) throws Exception {

        ZMQServer<double[],Double> server = new ZMQServer<>("com.serviceB.ServiceImpl","servicea");
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(server);

    /**
        Class service = Bar.class.getClassLoader().loadClass("com.serviceB.ServiceImpl");

       Object serviceObj = service.newInstance();

        Method m  = service.getDeclaredMethods()[0];
        double [] nums = {1.0,2.0};
        System.out.println( m.invoke(serviceObj,nums));
         */



    }

}
