open module generator {
    requires net.bytebuddy;
    requires service.api;

    requires jeromq;
    requires service.a;
    requires java.instrument;
    exports com.bytebuddy;
    exports com.poet;
    requires com.squareup.javapoet;
    requires java.compiler;
    requires java.logging;
}