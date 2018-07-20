
module app {
    requires service.api;
    uses api.CalcService;
    uses api.SayHelloService;

}