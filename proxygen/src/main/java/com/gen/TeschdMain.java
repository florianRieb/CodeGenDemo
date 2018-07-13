package com.gen;

import com.helper.ProxyClient;

public class TeschdMain {

    public static void main(String... args) throws Exception {
    ProxyClient<Double> client = new ProxyClient<>("servicea");

    double[] nums = {1.0,9.0};
        System.out.println(client.send(nums));



    }


}
