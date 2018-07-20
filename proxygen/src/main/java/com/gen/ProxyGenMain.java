package com.gen;

import java.io.*;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;

import java.nio.file.*;
import java.security.Provider;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class ProxyGenMain {
    private final static Logger LOGGER = Logger.getLogger(ProxyGenMain.class.getName());
    static Map<String,Path> mlibSourcees = new HashMap<>();

    public static void main(String... args) throws IOException, ClassNotFoundException {

        /**
         * Benötigt als Eingabe 2 Strings
         * arg[1] app/com.main.Main - Name des Moduls mit Packet und Klassenbezeichnung der Main
         * arg[0] Verzeichnis indem das Projekt liegt
         * arg[2] OPTIONAL Ordner indem die Dependenzies liegen z.B. weitere Frameworks etc. als jar Dateien und Modul kompatibel
         */

        //Got project dir = user.dir
         String  args0 = System.getProperty("user.dir");
         String arg1 = "app/com.main.Main";
         String workDir = args0+File.separator+"generatedFiles"+File.separator;
         String rootModule = arg1.substring(0,arg1.indexOf("/"));
        TempGenerator temp = new TempGenerator();

        createWorkDirs(workDir);




        // hier werden die Module des Projektes mit Maven gepackt  -> mvn package -f <args0> -DoutputDirectory=/mlib
        RuntimeExecutor runExec = new RuntimeExecutor();
        runExec.mvnCompile(args0);

        //Kopiere Mod in mlib in der Projektstruktur
        /**
        File modSource = new File(System.getProperty("user.dir")+"/proxygen/src/main/resources/mods/");
        File modDest = new File(System.getProperty("user.dir") +"/mlib/");
        copyFolder(modSource,modDest);
        */

        Map<String,List<String>> modules = new HashMap<>();
        Map<String,Map<String,Path>> environments = new HashMap<>();
        ModuleFinder finder = ModuleFinder.of(Paths.get(args0+ "/mlib"));

        //Speicher Modulename und Path aller Module in mlib
        finder.findAll().stream().forEach((ModuleReference ref) -> {
            mlibSourcees.put(ref.descriptor().name(),Paths.get(ref.location().get().getPath()));
        });

        if(finder.find(rootModule).isPresent()){
            ModuleDescriptor desc = finder.find(rootModule).get().descriptor();
            Map<String,Path> dependencies = readRequiredModules(desc);
            // App selbst hinzufügen
            dependencies.put(desc.name(), mlibSourcees.get(desc.name()));
            if(dependencies.keySet().size()>0){
                environments.put(desc.name(),dependencies);
            }
        }else  {
            LOGGER.warning("Module "+ rootModule + " can't be found in /mlib Dir");
        }


        //Suche nur nach Service-Modulen
        finder.findAll().stream().filter((ModuleReference ref) -> ref.descriptor().provides().size()>0)
                .forEach((ModuleReference modRef) -> {
                    List<String> lokalList = new LinkedList<>();


                    // ToDo Hier können die Constrains aus dem Resource Ordner der Module ausgelesen werden!

                   // Suche die Klassen welche den Service implementieren
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
            Optional<Module> opModule = newLayer.findModule(entry.getKey());


            if(opModule.isPresent()){
                Module m = opModule.get();


            for(String className:entry.getValue()){
                Class c = m.getClassLoader().loadClass(className);
                String destinationDir = workDir + File.separator+ "java" + File.separator + m.getName();
                //Vosicht!! Wenn mehrere Services in einem Moduel vorhanden sind, muss das module-info file anschließend erstellt werden
                // so würden für jede klasse die eien Service impl. ein eigenes erstellt werden.
                genClientModule(m,c,destinationDir);
                genServerModule(m,c,destinationDir);

                }
                //Für jedes generierte (server)Modul soll eine Sammlung aller Abhängigkeiten angelegt werden um eine lauffähige Anw. erstellen zu können.

               Map<String,Path> dependencies = readRequiredModules(m.getDescriptor());
                if(dependencies.keySet().size()>0){
                    environments.put(m.getName(),dependencies);
                }

            }
        }// end for (service modules)


        //Erstelle Compile-skript
        temp.skript(args0,modules.keySet());


        //Skript ausführen

        if(!runExec.execSkript())
            LOGGER.severe("Compilation failed");


        //Wenn Skipt erfolgreich ausgeführt liegen die jars in dem Verzeichnis /mods
        if((Paths.get(workDir+"mods" + File.separator + "server").iterator().hasNext())){
            //künftig über Path statt mit Files arbeiten -> File gilt als deprecated da nicht  nio2 unterstützt
           Path serverMods =  Paths.get(workDir+"mods" + File.separator + "server" + File.separator);

            Files.walk(serverMods).filter((Path p) -> p.toString().endsWith(".jar")).forEach((Path p) ->{
                String moduleName = p.toFile().getName();
                environments.get(moduleName.substring(0,moduleName.indexOf("_"))).put(p.toFile().getName().substring(0,p.toFile().getName().indexOf(".jar")),p);
            } );
        }

        System.out.println(environments);
        //ToDo erstelle ein Verzeichnis  je Env und kopiere alle Jars rein
        //ToDo anschließend Dockerfile erstellen

        createEnvDirs(workDir,environments,rootModule);

    }//main()

    public static void genClientModule(Module module, Class serviceImpl, String workDir) throws IOException {
        String clientDir = workDir + File.separator + "_client";

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

        String[] workdirs = new String[7];
        workdirs[0] = workDir;
        workdirs[1] = workDir+"java";
        workdirs[2] = workDir+"classes";
        workdirs[3] = workDir+"mods";
        workdirs[4] =  workDir+"mods"+File.separator+"client";
        workdirs[5] =  workDir+"mods"+File.separator+"server";
        workdirs[6] = workDir+"environments";
        for(int i = 0; i<workdirs.length ;i++){
            if(!new File(workdirs[i]).exists()){
                new File(workdirs[i]).mkdir();
                System.out.println("Directory created :: " + workdirs[i]);

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
            if(!destinationFolder.exists()){
                destinationFolder.mkdirs();
                System.out.println("Directory created :: " + destinationFolder);
            }

            //Copy the file content from one place to another
            Files.copy(sourceFolder.toPath(), destinationFolder.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied :: " + destinationFolder);
        }
    }

    private static Map<String,Path>  readRequiredModules(ModuleDescriptor modDesc){
        Map<String,Path> serviceDep= new HashMap<>();
        Set<ModuleDescriptor.Requires> requires =  modDesc.requires().stream()
                .filter((ModuleDescriptor.Requires req) -> !req.name().startsWith("java."))
                .filter((ModuleDescriptor.Requires req) -> !req.name().startsWith("javax"))
                .collect(Collectors.toSet());
        for(ModuleDescriptor.Requires r: requires){
            if (mlibSourcees.get(r.name()) != null){
                serviceDep.put(r.name(),mlibSourcees.get(r.name()));

            }else
            {
                //sobald eine Abhängigkeit nicht gefunden wurde ist der Server nicht lauffähig
                LOGGER.warning("Von " +modDesc.name() +" benötigtes Module " + r.name() + "nicht im Verzeichnis mlib gefunden");
                return null;
            }

        }
        return serviceDep;

    }

    private static void createEnvDirs(String workDir,Map<String,Map<String,Path>> environments, String rootModule ){
        Map<String,Path> tempEnv;
        File modSource = new File(System.getProperty("user.dir")+"/proxygen/src/main/resources/mods/");
        //workdir = projectDir + /generatedFiles;
        String envDir = workDir + File.separator +"environments" + File.separator;
        //rootModule is the main application
        Map<String,Map<String,Path>> envis = environments;
        //env-0 should always include the main application

        String envX = "env";
        int i = 0;

        tempEnv = envis.remove(rootModule);
        for(Map.Entry<String,Path> entry: tempEnv.entrySet()){
            //generatedFiles/environments/env0/<...>.jar
                //dependencies
            Path destPath =  Paths.get(envDir + envX + i + File.separator + entry.getKey()+".jar");
                //helper
            Path desPath2 = Paths.get(envDir + envX + i);
                //clients
            Path clientsource = Paths.get(workDir + File.separator + "mods" + File.separator + "client");
            try {
                copyFolder(entry.getValue().toFile(),destPath.toFile());
                copyFolder(modSource,desPath2.toFile());
                copyFolder(clientsource.toFile(),desPath2.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        for(String key: envis.keySet()){
            i++;
              for(Map.Entry<String,Path> entry: envis.get(key).entrySet()){
                  Path destPath =  Paths.get(envDir + envX + i + File.separator + entry.getKey()+".jar");
                  Path desPath2 = Paths.get(envDir + envX + i);
                  try {
                      copyFolder(entry.getValue().toFile(),destPath.toFile());
                      copyFolder(modSource,desPath2.toFile());
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
        }




    }

}//class
