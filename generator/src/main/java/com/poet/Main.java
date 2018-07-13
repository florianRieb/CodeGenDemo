package com.poet;


import api.CalcService;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {

    public static void main(String... args){


        Set<Module> serviceModules = new HashSet<>();
        serviceModules = ModuleLayer.boot().modules().stream().filter(isService()).collect(Collectors.toSet());
        Optional<Module> thisModule = ModuleLayer.boot().findModule("generator");
        Module localM = null;
        if (thisModule.isPresent())
             localM =  thisModule.get();



        System.out.println("Services count " +serviceModules.size());


        Module tempModule = serviceModules.iterator().next();


        List<Class> classes = loadModuleClasses("app");
        System.out.println(classes.get(0).getName());
        System.out.println(classes.get(0).getProtectionDomain().getCodeSource().getLocation().getPath());
        System.out.println(classes.get(0).getInterfaces()[0].getDeclaredMethods()[0]);



        // API muss bekannt sein, da die Client Klasse diese implementieren muss!



        Class api = CalcService.class;


        Method[] declaredMethods =  api.getDeclaredMethods();


        Generator generator = new Generator();
        MethodSpec m = generator.genProxyMethod(declaredMethods[0]);
        Set<MethodSpec> methods = new HashSet<>();
        methods.add(m);

        try {
            generator.saveJavaFile("com.serviceB",generator.genClass("ServiceImpl", api,methods));
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    public static Predicate<Module> isService() {
        // hier muss die Auswahl der benötigten Module zur Verfügung stehen
        return d -> d.getDescriptor().name().startsWith("service");
    }


    public static List<Class> loadModuleClasses(String modName){
        System.out.println("Modulname: "+ modName);

        String p = System.getProperty("user.dir")+"/"+modName+"/target/classes";
        System.out.println(p);

        ModuleFinder finder = ModuleFinder.of(Paths.get(p));
        //ModuleFinder finder = ModuleFinder.of(Paths.get(System.getProperty("user.dir")+"/mlib"));
        Optional<ModuleReference> optionalModuleReference = finder.find(modName);
        ModuleReference moduleRef = optionalModuleReference.get();


        try(ModuleReader modulreReader = moduleRef.open()){
            return modulreReader.list().filter(name -> name.endsWith("Main.class"))
                    .map(Main::classLoadByFileName)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(new IOException(e));
        }

    }

    private static Class classLoadByFileName(String fileName){
        ClassLoader cLoader = Main.class.getClassLoader();
        String nameOfClass = fileName.substring(0,fileName.length()-".class".length());
        try{
            nameOfClass = nameOfClass.replace("/",".");
            return cLoader.loadClass(nameOfClass);

        } catch (ClassNotFoundException e) {
            throw new UncheckedIOException(new IOException(e));
        }

    }

}
