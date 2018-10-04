package com.service;

import api.CalcService;

import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServiceImpl implements CalcService  {

        private static Logger LOGGER= LogManager.getLogManager().getLogger("");

        @Override
        public Double calc(double[] numbers) throws Exception{

            double sum = 0;
            for(double n:numbers){
                sum+=n;
            } LOGGER.info("Service A received numbers an calc sum "+sum);
            return sum;
        }


}
