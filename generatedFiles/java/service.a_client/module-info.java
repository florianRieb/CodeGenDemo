module service.a_client {
requires java.logging;
requires libmodule;
requires service.api;
requires java.base;

exports com.service;

provides api.CalcService with com.service.ServiceImpl;
}