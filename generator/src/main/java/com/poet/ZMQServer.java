package com.poet;

import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import java.io.*;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class ZMQServer<I,O>  implements Runnable{


    static final Logger LOGGER =  Logger.getLogger(ZMQServer.class.getName());

    ZMQ.Context ctx;
    ZMQ.Socket server;
    ByteArrayInputStream bis;
    ObjectInput in = null;
    //double[] o = null;
    I o = null;





    public ZMQServer(){

    }

    @Override
    public void run(){
        this.ctx = ZMQ.context(1);
        this.server = ctx.socket(ZMQ.REP);
        this.server.bind("ipc://remoteservice");
        System.out.println("start runnung");

        while(!Thread.currentThread().isInterrupted()){


            ZMsg recvMsg = ZMsg.recvMsg(this.server);
            System.out.println("Msg size:" + recvMsg.size());
            if(recvMsg.size()>=1){
                LOGGER.info("server received a message consists of "+  recvMsg.size() +"frames ");
                System.out.println("Msg size:" + recvMsg.size());

                //lese Nachricht aus
                Iterator<ZFrame> frames = recvMsg.iterator();
                while(frames.hasNext()){
                    ZFrame frame = frames.next();
                    //byte[] -> Object
                    System.out.println("get ByteArry size: " + frame.getData().length);
                    bis =  new ByteArrayInputStream(frame.getData());
                    try {
                        in = new ObjectInputStream(bis);
                        o = ((I) in.readObject());
                        //ToDo hier kann Service Methdde aufgerufen werden
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }

            System.out.println("replying");
            //Send Server reply
            Double d = 444.4;
            byte[] returnByte = null;
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutput out = null;


                out = new ObjectOutputStream(bos);
                out.writeObject(d);
                out.flush();
                //System.out.println("ReturnByte [] size "+returnByte.length);
                returnByte = bos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("send");
            this.server.send(returnByte);

            System.out.println(returnByte.length);



        }

    }



    public static void main(String... args){

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(new ZMQServer<Double[],Double>());


    }




}
