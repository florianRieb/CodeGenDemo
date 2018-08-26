module service.b {

    requires service.api;
    requires java.logging;

    provides api.CalcService with com.serviceB.ServiceImpl;

}