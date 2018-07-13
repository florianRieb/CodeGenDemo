package com.service;

import api.CalcService;

import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ServiceImpl implements CalcService  {

        private static Logger LOGGER= LogManager.getLogManager().getLogger("");

        @Override
        public double calc(double[] numbers) {
            LOGGER.info("ServiceA Got numbers and calc sum");
            double sum = 0;
            for(double n:numbers){
                sum+=n;
            }
            return sum;
        }

}
