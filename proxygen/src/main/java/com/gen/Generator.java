package com.gen;


import com.helper.ProxyClient;
import com.helper.ZMQServer;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Generator {

    public void genClientClass(String moduleName, Class providerClass, String desDir){
        Method[] methods = providerClass.getDeclaredMethods();
        Set<MethodSpec> proxyMethodSet = new HashSet<>();

        MethodSpec[] proxyMethods = new MethodSpec[methods.length];
        for(Method m: methods){
            proxyMethodSet.add(genProxyMethod(m,moduleName));
        }

        TypeSpec proxyClass = genClass(providerClass.getSimpleName(),providerClass.getInterfaces(),proxyMethodSet);
        try {
            saveJavaFile(moduleName,providerClass.getPackageName(),proxyClass, desDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void genServerClass( Class serviceClass,String desDir){

        Method method = serviceClass.getDeclaredMethods()[0];


       MethodSpec main = MethodSpec.methodBuilder("main")
               .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
               .returns(void.class)
               .addParameter(String[].class, "args")
               .addStatement("$T<$T,$T> serv = new $T($S,$S)"
                       , ZMQServer.class,method.getParameterTypes()[0],getWrapper(method.getReturnType()),ZMQServer.class,
                       serviceClass.getName(), serviceClass.getModule().getName())
               .addStatement("$T executor = $T.newFixedThreadPool($L)", ExecutorService.class, Executors.class,2)
               .addStatement("executor.submit(serv)")
               .addException(ClassNotFoundException.class)
               .addException(IllegalAccessException.class)
               .addException(InstantiationException.class)
               .build();

       // ZMQServer<double[],Double> server = new ZMQServer<>("com.serviceB.ServiceImpl","servicea");

        TypeSpec serverClass = TypeSpec.classBuilder("Server").addModifiers(Modifier.PUBLIC).addMethod(main).build();
        JavaFile file = JavaFile.builder(serviceClass.getPackageName(),serverClass).build();
        try {
            file.writeTo(Paths.get(desDir));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private MethodSpec genProxyMethod(Method superMethod, String moduleName){

        Set<ParameterSpec> parr = new HashSet<>();
        int i = 0;
        for(Class c:superMethod.getParameterTypes()){
            String arg = "arg"+i;
            parr.add(ParameterSpec.builder(c,arg).build());
            i++;
        }

        Set<TypeName> exSet =  new HashSet<>();
        for(Class ex:superMethod.getExceptionTypes()){
            exSet.add(ParameterizedTypeName.get(ex));
        }

        Iterable<ParameterSpec> param = parr;
        Iterable<TypeName> exceptions = exSet;

        Class returnType = superMethod.getReturnType();
        String methodNam = superMethod.getName();


        Class genType =(returnType.isPrimitive()) ? getWrapper(returnType) : returnType;

        //ToDo Übermittle alle Parameter NICHT NUR DEN ERSTEN!
        return MethodSpec.methodBuilder(methodNam).addModifiers(Modifier.PUBLIC)
                .returns(returnType)
                .addParameters(param)
                .addAnnotation(Override.class)
                .addStatement("$T<$T> client = new $T<>($S)", ProxyClient.class,genType ,ProxyClient.class,moduleName)
                .addStatement("return client.send(arg0)")
                .addExceptions(exceptions)
                .build();

    }

    private TypeSpec genClass(String className, Class[] superInterfaces, Set<MethodSpec> methods) {
        Set<TypeName> interfaces = new HashSet<>();

        for(Class c:superInterfaces){
            if(c==null) break;

            interfaces.add(ParameterizedTypeName.get(c));
        }

        Iterable<TypeName> itrInterfaces= interfaces;
        Iterable<MethodSpec> itrMethods = methods;



        TypeSpec.Builder builder = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC);
        if(methods.size()>=1){
            builder
            .addSuperinterfaces(itrInterfaces)
            .addMethods(itrMethods);

        }

        return builder.build();
    }


    private void saveJavaFile(String modulName, String packageName, TypeSpec clazz, String destDir) throws IOException {

        JavaFile file = JavaFile.builder(packageName,clazz).build();
        file.writeTo(Paths.get(destDir));

    }


    private Class getWrapper(Class primitiv){
        switch(primitiv.getSimpleName()){
            case "double": return Double.class;

            case "int": return Integer.class;

            case "char": return Character.class;

            case "float": return Float.class;

            case "boolean": return Boolean.class;

            case "long": return Long.class;

            default: return null;
            }
    }



}
