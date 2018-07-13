module service.b {

requires proxygenclient;
requires jeromq;
requires java.base;
requires service.api;
requires proxygen;

exports com.serviceB to[proxygenclient];

provides api.CalcService with com.serviceB.ServiceImpl;

}