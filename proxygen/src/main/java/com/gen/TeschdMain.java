package com.gen;

import com.helper.ProxyClient;
import com.helper.ZMQServer;

import java.io.DataOutput;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeschdMain {

    public static void main(String... args) throws Exception {
        String args0 = System.getProperty("user.dir");

        List<Callable<Boolean>> serverList = new LinkedList<>();
        //ZMQServer<double[],Double> serv = new ZMQServer<>("test.ServiceImpl","service.b");
        serverList.add(new ZMQServer<double[],Double>("test.ServiceImpl","service.b"));
        ZMQServer<double[],Double> serv2 = new ZMQServer<>("test.ServiceZwei","servicezwei");
        //serverList.add(serv);
        serverList.add(serv2);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.invokeAll(serverList);



    }

}
