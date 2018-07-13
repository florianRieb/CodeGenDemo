package com.poet;


import api.CalcService;
import com.squareup.javapoet.*;
import net.bytebuddy.implementation.MethodDelegation;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Generator {





    public MethodSpec genProxyMethod(Method superMethod){

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
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .addStatement("$T<$T> client = new $T<>($S)",ProxyClient.class,genType ,ProxyClient.class,"remoteService")
                .addStatement("return client.send(arg0)")

                .addExceptions(exceptions)
                .build();

    }

    public TypeSpec genClass(String className,Class superInterface, Set<MethodSpec> methods) {
        TypeSpec.Builder builder = TypeSpec.classBuilder(className).addModifiers(Modifier.PUBLIC);
        if(methods.size()>=1){
            Iterable<MethodSpec> itr = methods;
            builder.addSuperinterface(superInterface).addMethods(itr);
        }

        return builder.build();
    }


    public void saveJavaFile(String packageName, TypeSpec clazz) throws IOException {
        JavaFile file = JavaFile.builder(packageName,clazz).build();
        //file.writeTo(Paths.get(System.getProperty("user.dir")+"/generator/src/main/java/"));
        file.writeTo(Paths.get(System.getProperty("user.dir")+"/service.b/src/main/java/"));

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
