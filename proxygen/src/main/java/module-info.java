module proxygenclient {
    requires freemarker;
    requires com.squareup.javapoet;
    requires java.compiler;
    requires jeromq;
    requires java.logging;

    exports com.helper;
}