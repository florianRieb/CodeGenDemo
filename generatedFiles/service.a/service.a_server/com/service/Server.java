package com.service;

import com.helper.ZMQServer;
import java.lang.Double;
import java.lang.String;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
  public static void Main(String[] args) {
    ZMQServer<double[],Double> serv = new ZMQServer("com.service.ServiceImpl","service.a");
    ExecutorService executor = new Executors.newFixedThreadPool(2);
    executor.submit(serv);
  }
}
