package com.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.Argument;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class ProxyInterceptor {


    @RuntimeType
    public static void intercept(@Argument(0) String[] arg,@Origin Method method ){
        System.out.println("String Interceptor");
    }

    @RuntimeType
    public static void intercept(@Argument(0) double[] arg, @Origin Method method){
        if(arg.length<=0)
            throw new IllegalArgumentException("Keine Parameter übergeben");
        System.out.println("Double interceptor!");
        for(double d:arg){
            System.out.println(d);
        }

        //ProxyClient<Double> client = new ProxyClient<>("servieremote");

        /**
        //Nur für den ersten Parameter!
        ZMsg reqMsg = new ZMsg();
        //numbers -> ZFrame -> ZMsg
        ByteBuffer buf = ByteBuffer.allocate(8);
        for(double d:arg){
            buf.putDouble(d);
            buf.flip();
            reqMsg.add(new ZFrame(buf.array()));
        }
        */


    }


}

