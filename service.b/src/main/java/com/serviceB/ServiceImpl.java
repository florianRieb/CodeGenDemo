package com.serviceB;

import api.CalcService;

import java.util.logging.LogManager;
import java.util.logging.Logger;


public class ServiceImpl implements CalcService {
  private static Logger LOGGER= LogManager.getLogManager().getLogger("");
  @Override
  public double calc(double[] arg0) throws Exception {

    LOGGER.info("ServiceB Got numbers and return: 5.0");
    return 5.0;
  }

}
