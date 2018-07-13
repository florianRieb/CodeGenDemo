package com.poet;

import java.lang.System;

public class ServiceImpl {
  public double calc(double par) {
    System.out.println("Hello, JavaPoet!");
    ProxyClient client = new ProxyClient("remoteService");
    return 5.0;
  }
}
