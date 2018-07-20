package com.say;

import api.SayHelloService;

public class HelloImpl implements SayHelloService {
    @Override
    public String sayHello(String msg) {
        return "Hello "+ msg +" im Service C";
    }
}
