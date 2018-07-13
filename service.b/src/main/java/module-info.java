module service.b {

    requires service.api;
    requires proxygenclient;
    requires jeromq;

    exports com.serviceB to proxygenclient;

    provides api.CalcService with com.serviceB.ServiceImpl;

}