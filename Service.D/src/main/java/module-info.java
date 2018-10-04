import api.CalcService;

module Service.D {
    requires service.api;
    requires commons.math3;
    requires java.logging;

    provides CalcService with com.ServiceImpl;

}