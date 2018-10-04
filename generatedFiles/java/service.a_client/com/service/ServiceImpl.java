package com.service;

import api.CalcService;
import com.helper.ProxyClient;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Override;

public class ServiceImpl implements CalcService {
  @Override
  public Double calc(double[] arg0) throws Exception {
    ProxyClient<Double> client = new ProxyClient<>("service.a");
    return client.send(arg0);
  }
}
