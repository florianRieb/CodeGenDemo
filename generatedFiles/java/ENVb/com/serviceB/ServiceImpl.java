package com.serviceB;

import api.CalcService;

import java.util.logging.LogManager;
import java.util.logging.Logger;


public class ServiceImpl implements CalcService {
  private static Logger LOGGER= LogManager.getLogManager().getLogger("");
  @Override
  public Double calc(double[] arg0) throws Exception {

    LOGGER.info("Service B received numbers an return 5 ");
    return 5.0;
  }

}
