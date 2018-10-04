package com;

import com.helper.ZMQServer;
import java.lang.Boolean;
import java.lang.ClassNotFoundException;
import java.lang.Double;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.InterruptedException;
import java.lang.String;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
  public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException,
      InstantiationException, InterruptedException {
    List<Callable<Boolean>> serverList = new LinkedList<>();
    ExecutorService executor = Executors.newFixedThreadPool(2);
    serverList.add(new ZMQServer<double[],Double>("com.service.ServiceImpl","service.a"));
    serverList.add(new ZMQServer<double[],Double>("com.serviceB.ServiceImpl","service.b"));
    executor.invokeAll(serverList);
  }
}
