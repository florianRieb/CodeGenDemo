module proxygen {
    requires freemarker;
    requires com.squareup.javapoet;
    requires java.compiler;
    requires libmodule;
    requires java.logging;
    requires java.desktop;
    requires service.api;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    exports test to libmodule;

    opens com.gen to com.fasterxml.jackson.databind;


}