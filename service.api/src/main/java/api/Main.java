package api;

import java.lang.reflect.Method;

public class Main {
    public static void main(String... args){
        Method[] methods = CalcService.class.getDeclaredMethods();

        System.out.println(methods[0].getReturnType());
        System.out.println(methods[0].getParameterTypes()[0]);



    }
}
