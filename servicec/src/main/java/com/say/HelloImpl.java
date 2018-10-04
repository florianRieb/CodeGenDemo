package com.say;

import api.SayHelloService;

public class HelloImpl implements SayHelloService {
    @Override
    public String sayHello(String msg1) {
        return "Hello im Service C and received  the following msg from you: "+ msg1;
    }
}
