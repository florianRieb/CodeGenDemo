package com.gen;

import java.io.*;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;

import java.nio.file.*;
import java.util.*;




public class ProxyGenMain {

    public static void main(String... args) throws IOException, ClassNotFoundException {

        //Got project dir = user.dir
        String args0 = System.getProperty("user.dir");
        String workDir = args0+File.separator+"generatedFiles"+File.separator;

        createWorkDirs(workDir);

        TempGenerator temp = new TempGenerator();
        temp.skript(workDir);


        //ToDo hier müssen die Module des Projektes mit Maven gepackt werden -> mvn package -f <args0> -DoutputDirectory=/mlib


        //Kopiere Mod in mlib in der Projektstruktur

        File modSource = new File(System.getProperty("user.dir")+"/proxygen/src/main/resources/mods/");
        File modDest = new File(System.getProperty("user.dir") +"/mlib/");
        copyFolder(modSource,modDest);

        Map<String,List<String>> modules = new HashMap<>();


        //Durchsuche die Module aus dem Projekt
        ModuleFinder finder = ModuleFinder.of(Paths.get(args0+ "/mlib"));
        finder.findAll().stream().filter((ModuleReference ref) -> ref.descriptor().provides().size()>0)
                .forEach((ModuleReference modRef) -> {
                    List<String> lokalList = new LinkedList<>();

            //Hier können die Constrains aus dem Resource Ordner der Module ausgelesen werden!
                    modRef.descriptor().provides()
                            .forEach((ModuleDescriptor.Provides p) -> lokalList.addAll(p.providers()));

                    modules.put(modRef.descriptor().name(),lokalList);


                });





        //Laden der Module und generieren des Source Codes
        ModuleLayer bootLayer = ModuleLayer.boot();
        Configuration config = bootLayer.configuration().resolve(finder,ModuleFinder.of(), modules.keySet());
        ClassLoader scl = ClassLoader.getSystemClassLoader();
        ModuleLayer newLayer = bootLayer.defineModulesWithOneLoader(config,scl);
        for(Map.Entry<String,List<String>> entry: modules.entrySet()){


            for(String className:entry.getValue()){
                Optional<Module> opModule= newLayer.findModule(entry.getKey());
                if(opModule.isPresent()){
                Module m = opModule.get();
                Class c = m.getClassLoader().loadClass(className);
                //ZMQServer<Double[],Double> server = new ZMQServer<>(c);
                String destinationDir = workDir+File.separator+ "java"+File.separator + m.getName();
                genClientModule(m,c,destinationDir);
                genServerModule(m,c,destinationDir);

                }



            }
        }


    }//main()

    public static void genClientModule(Module module, Class serviceImpl, String workDir) throws IOException {
        String clientDir = workDir + File.separator + "_client";
        //Zuerst die Klass-datei anlegen, sonst findet Freemarker das Verzeichnis nicht
        Generator generator = new Generator();
        generator.genClientClass(module.getName(),serviceImpl,clientDir);
        TempGenerator moduleInfoGen = new TempGenerator();
        moduleInfoGen.cloneModuleInfo(module.getDescriptor(),clientDir,"");

    }
    public static void genServerModule(Module module, Class serviceImpl, String workDir) throws IOException {
        String serverDir = workDir + File.separator  + "_server";
        String packageName = serviceImpl.getPackageName().replace(".",File.separator);
        String fileName = serviceImpl.getName().replace(".",File.separator);
        //Erstelle Verzeichnis
        Files.createDirectories(Paths.get(serverDir+ File.separator+packageName,File.separator));
        //Kopiere ServiceImpl Klasse
        Path serviceImplPath  =  Paths.get(System.getProperty("user.dir")+ File.separator+module.getName()+File.separator+"src/main/java/"+fileName+".java");
        Path serverModulePath = Paths.get(serverDir+File.separator+ fileName+".java");
        Files.copy(serviceImplPath,serverModulePath, StandardCopyOption.REPLACE_EXISTING);

        Generator generator = new Generator();
        generator.genServerClass(serviceImpl,serverDir);
        //Erstelle module-info mit qualified export zu proxygen - So hat ZMQServer Zugriff auf die ServiceImpl.class
        TempGenerator moduleInfoGen = new TempGenerator();
        moduleInfoGen.cloneModuleInfo(module.getDescriptor(),serverDir,serviceImpl.getPackageName());
    }


    private static void createWorkDirs(String workDir){

        String[] workdirs = new String[4];
        workdirs[0] = workDir;
        workdirs[1] = workDir+"java";
        workdirs[2] = workDir+"classes";
        workdirs[3] = workDir+"mods";
        for(String s:workdirs){
            if(!new File(s).exists()){
                new File(s).mkdir();
                System.out.println("Directory created :: " + s);

            }
        }



    }

    private static void copyFolder(File sourceFolder, File destinationFolder) throws IOException
    {
        //Check if sourceFolder is a directory or file
        //If sourceFolder is file; then copy the file directly to new location
        if (sourceFolder.isDirectory())
        {
            //Verify if destinationFolder is already present; If not then create it
            if (!destinationFolder.exists())
            {
                destinationFolder.mkdir();
                System.out.println("Directory created :: " + destinationFolder);
            }

            //Get all files from source directory
            String files[] = sourceFolder.list();

            //Iterate over all files and copy them to destinationFolder one by one
            for (String file : files)
            {
                File srcFile = new File(sourceFolder, file);
                File destFile = new File(destinationFolder, file);

                //Recursive function call
                copyFolder(srcFile, destFile);
            }
        }
        else
        {
            //Copy the file content from one place to another
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied :: " + destinationFolder);
        }
    }





}//class
