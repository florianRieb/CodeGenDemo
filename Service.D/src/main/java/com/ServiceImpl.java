package com;


import api.CalcService;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServiceImpl implements CalcService {
    private static Logger LOGGER= LogManager.getLogManager().getLogger("");
    @Override
    public Double calc(double[] n) throws Exception {
        DescriptiveStatistics descStat = new DescriptiveStatistics();
        if(n.length>0){
          for(double d:n){
              descStat.addValue(d);
          }
          LOGGER.info("Service D received numbers an return maximum: " + descStat.getMax());
          return descStat.getMax();
        }

        return -1.0;
    }
}
