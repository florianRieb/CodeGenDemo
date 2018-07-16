package com.gen;

import com.helper.ProxyClient;
import com.helper.ZMQServer;

import java.io.DataOutput;

public class TeschdMain {

    public static void main(String... args) throws Exception {
    ProxyClient<Double> client = new ProxyClient<>("servicea");

    double[] nums = {1.0,9.0};
        System.out.println(client.send(nums));

    }
    ZMQServer<double[], Double> server;

    {
        try {
            server = new ZMQServer<>("ServiceImpl", "service.a");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }


}
