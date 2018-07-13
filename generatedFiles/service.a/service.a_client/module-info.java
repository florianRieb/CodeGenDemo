module service.a {

requires java.base;
requires service.api;
requires java.logging;
requires proxygen;

exports com.service;

provides api.CalcService with com.service.ServiceImpl;

}