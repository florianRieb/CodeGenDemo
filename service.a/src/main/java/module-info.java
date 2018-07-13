module service.a {
    exports com.service;
    requires service.api;
    requires java.logging;

    provides api.CalcService with com.service.ServiceImpl;
}