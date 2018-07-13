package com.serviceB;

import api.CalcService;
import com.helper.ProxyClient;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Override;

public class ServiceImpl implements CalcService {
  @Override
  public double calc(double[] arg0) throws Exception {
    ProxyClient<Double> client = new ProxyClient<>("service.b");
    return client.send(arg0);
  }
}
