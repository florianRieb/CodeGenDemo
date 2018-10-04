module service.b_client {
requires java.logging;
requires libmodule;
requires service.api;
requires java.base;


provides api.CalcService with com.serviceB.ServiceImpl;
}