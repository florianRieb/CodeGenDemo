module servicec {

    requires service.api;

    provides api.SayHelloService with com.say.HelloImpl;


}