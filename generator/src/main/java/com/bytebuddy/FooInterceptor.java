package com.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;

import java.lang.reflect.Method;

public class FooInterceptor {
    static FooInterceptor dispatcher = new FooInterceptor();

    @RuntimeType
    public static void doIntercept(@Argument(0) String[] arg, @Origin Method method){

        dispatcher.intercept("String");

    }

    @RuntimeType
    public static void doIntercept(@Argument(0) double[] arg, @Origin Method method){

        double d = dispatcher.intercept("double");


    }

    public double intercept(String typ){
        System.out.println(typ+" Interceptor called");

       InnerClass c = new InnerClass();
       //FooInterceptor.InnerClass d = this.new InnerClass();


        return 1.0;
    }

    class InnerClass{

        private final static int REQUEST_TIMEOUT = 2500;
        private final static int REQUEST_RETRIES = 3;
        private String SERVER_ENDPOINT;

        private ZContext ctx;
        private  Socket client;
        private  double avg = 0;

        InnerClass(){
            this.ctx = new ZContext();
            this.client = ctx.createSocket(ZMQ.REQ);

        }




    }

}
