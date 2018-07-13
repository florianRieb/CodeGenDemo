package com.poet;


import org.zeromq.ZContext;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZMsg;

import java.io.*;
import java.nio.channels.Selector;


public class ProxyClient<R> {
    private final static int REQUEST_TIMEOUT = 2500;    //  msecs, (> 1000!)
    private final static int REQUEST_RETRIES = 3;       //  Before we abandon
    private static String SERVER_ENDPOINT;


    private static ZContext ctx;
    private static Socket client;
    private double avg = 0;

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ObjectOutput out = null;
    byte[] byteArr= null;


    public ProxyClient(String serviceName){
        System.out.println("Create Client");

        this.SERVER_ENDPOINT = "ipc://"+serviceName;
        this.ctx = new ZContext();
        this.client = ctx.createSocket(ZMQ.REQ);
        assert (client != null);
        //build connection
        System.out.println(client.connect(SERVER_ENDPOINT));


    }


    public R send(Object obj) throws Exception {
        if(!(obj instanceof Serializable))
            throw new Exception("Object not implements Serializable");

        //Serialisierung  des Obj und senden der Msg
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            byteArr = bos.toByteArray();

            ZMsg reqMsg = new ZMsg();
            reqMsg.add(new ZFrame(byteArr));
            R value = invoke(reqMsg);
            System.out.println(value.toString());
            return value;

        }catch (IOException ex){

        }
        return null;

    }

    public R invoke(ZMsg input) {
        System.out.println("Client invoke");
        int retriesLeft = REQUEST_RETRIES;
        while (retriesLeft>0 && !Thread.currentThread().isInterrupted()){
            System.out.println("Msg size:" + input.size());
            input.send(client);

            int expect_reply = 1;
            while (expect_reply > 0) {
                //  Poll socket for a reply, with timeout
                ZMQ.PollItem items[]  = {new ZMQ.PollItem(client, ZMQ.Poller.POLLIN)};
                items[0] = new ZMQ.PollItem(client, ZMQ.Poller.POLLIN);
                Selector selector = null;
                try {
                    selector = Selector.open();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int rc = ZMQ.poll(selector,items, REQUEST_TIMEOUT);

                if (rc == -1) break;          //  Interrupted

                //  Here we process a server reply and exit our loop if the
                //  reply is valid. If we didn't a reply we close the client
                //  socket and resend the request. We try a number of times
                //  before finally abandoning:

                if (items[0].isReadable()) {
                    //  We got a reply from the server, must match sequence
                    byte[]reply = client.recv();



                    if (reply == null)
                        break;      //  Interrupted


                    if (reply.length>0) {
                        System.out.println("reply length: "+reply.length );

                        ByteArrayInputStream bis = new ByteArrayInputStream(reply);
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

                        return (R)o;

                        //retriesLeft = 0 ;
                        //expect_reply = 0;

                    } else
                        System.out.printf("E: malformed reply from server: %s\n",
                                reply);

                } else if (--retriesLeft == 0) {
                    System.out.println("E: server seems to be offline, abandoning\n");
                    this.avg = -1;
                    break;
                } else {
                    System.out.println("W: no response from server, retrying\n");
                    //  Old socket is confused; close it and open a new one
                    ctx.destroySocket(client);
                    System.out.println("I: reconnecting to server\n");
                    client = ctx.createSocket(ZMQ.REQ);
                    client.connect(SERVER_ENDPOINT);
                    //  Send request again, on new socket
                    input.send(client);
                }
            }


        }

        return null;
    }



}
