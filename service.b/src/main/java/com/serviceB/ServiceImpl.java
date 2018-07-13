package com.serviceB;

import api.CalcService;
//import com.helper.ProxyClient;

public class ServiceImpl implements CalcService {
  @Override
  public double calc(double[] arg0) throws Exception {
    //ProxyClient<Double> client = new ProxyClient<>("service.b");
    //return client.send(arg0);
      System.out.println("Method was called");
    return 5.0;
  }
}
