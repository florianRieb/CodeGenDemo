module ENVb {
requires java.logging;
requires libmodule;
requires service.api;
requires java.base;

exports com.serviceB to libmodule;
exports com.service;

}