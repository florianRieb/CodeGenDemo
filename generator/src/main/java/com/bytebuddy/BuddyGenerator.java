package com.bytebuddy;


import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.MethodDelegation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class BuddyGenerator {


    /**
     * Braucht service.b

    String sourceDir = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    String serviceDir = ServiceImpl.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    String packageDir = this.getClass().getPackageName().replace(".","/");
    String fileDir = sourceDir+packageDir;
    String serviceFileDir = serviceDir;


    void doStuffAndSave(){



        byte[] classBytes = new ByteBuddy()
                .redefine(ServiceImpl.class)
                .method(named("calc"))
                .intercept(FixedValue.value(42.2))
                .make()
                .getBytes();


        try {


            Files.write(Paths.get(serviceFileDir+ServiceImpl.class.getName().replace(".","/") +".class"), classBytes, StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void delegateMethod(){

        AgentBuilder builder = new AgentBuilder.Default()
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.InitializationStrategy.NoOp.INSTANCE)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE);
        

        new ByteBuddy().redefine(ServiceImpl.class).method(named("calc"))
                .intercept(MethodDelegation.to(FooInterceptor.class)).make()
                .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.INJECTION).getLoaded();



    }
    */


}
