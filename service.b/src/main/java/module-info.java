module service.b {

    requires service.api;

    provides api.CalcService with com.serviceB.ServiceImpl;

}