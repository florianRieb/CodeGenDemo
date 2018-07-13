package com.poet;

import api.CalcService;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Override;
import java.lang.System;

public class ServiceImpl2 implements CalcService {
  ProxyClient<Double> client;

  public ServiceImpl2 (){
    ProxyClient<Double> client = new ProxyClient("remoteService");

  }
  @Override
  public double calc(double[] par) throws Exception {
    System.out.println("Hello, JavaPoet!");

    return client.send(par);
  }
}
