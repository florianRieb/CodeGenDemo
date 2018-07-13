package com.bytebuddy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.module.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Finder {

    private String modName;
    ModuleFinder finder;
    ModuleReference modRef;

    public Finder (){
        this.finder = ModuleFinder.of(Paths.get(System.getProperty("user.dir")+"/mlib"));

    }


    public ModuleDescriptor find(String modName){
        this.modRef = finder.find(modName).get();
        return modRef.descriptor();


    }

    public List<String> getClassName(){

        String className;

        try (ModuleReader reader = this.modRef.open()){
            { return reader.list().filter(name -> name.endsWith(".class")).filter(name -> !name.startsWith("module-info")).collect(Collectors.toList());
                    //.map(Finder:: classLoadByFileName).collect(Collectors.toList());

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;


    }

    private static Class<?> classLoadByFileName(String classFileName){
        ClassLoader classLoader = Finder.class.getClassLoader();
        String nameOfClass = classFileName.substring(0,classFileName.length()-".class".length());
        try{
            nameOfClass = nameOfClass.replace('/','.');
            return classLoader.loadClass(nameOfClass);
        }catch(ClassNotFoundException classNotFoundException){
            throw new UncheckedIOException(new IOException(classNotFoundException));

        }

    }
}
