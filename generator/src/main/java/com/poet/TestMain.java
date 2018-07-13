package com.poet;

import java.io.*;


public class TestMain {

    public static void main(String... args) throws Exception {

        double[] array = {10.0, 5.0,99.0};
        Double i = 1.0;
        String[] arr = {"hello", "World"};
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] yourBytes = null;

        //Runnable server = new ZMQServer<Double[],Double>();


        ProxyClient<Double> client = new ProxyClient<>("remoteservice");

        Double d = client.send(array);
        System.out.println(d);


        /**
        ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
        ObjectInput in = null;
        Object o = null;
        try {
            in = new ObjectInputStream(bis);
             o = in.readObject();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        System.out.println(o.getClass().getSimpleName());




       if(isArray(array)){
           for(Object obj:arr){
               double d = (obj instanceof Double) ? ((Double) obj) : -1;
               System.out.println(d);
               if (d == -1){
               String s = (obj instanceof String) ? ((String) obj) : null;
                   System.out.println(s);
               }

           }
       }
       */

    }

    public static boolean isArray(Object obj)
    {

        return obj!=null && obj.getClass().isArray();
    }

}
