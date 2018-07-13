package com.serviceB;

import com.helper.ZMQServer;
import java.lang.Double;
import java.lang.String;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
  public static void Main(String[] args) {
    ZMQServer<double[],Double> serv = new ZMQServer("com.serviceB.ServiceImpl","service.b");
    ExecutorService executor = new Executors.newFixedThreadPool(2);
    executor.submit(serv);
  }
}
