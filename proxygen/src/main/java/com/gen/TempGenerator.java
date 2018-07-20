package com.gen;

import freemarker.template.Configuration;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Path;
import java.util.*;

public class TempGenerator {

    Configuration cfg;

    public TempGenerator(){
        //Freemarker Test
       this.cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_28);
        try {
            cfg.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir")+"/proxygen/src/main/resources/templates"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
    }


    public void cloneModuleInfo(ModuleDescriptor desc, String fileDest, String exportPackageName) throws IOException {

        //Create Data Model
        Map<String,Object> root = new HashMap<>();
        List<String> requires= new LinkedList<>();
        List<String> exports = new LinkedList<>();
        List<String> opens = new LinkedList<>();
        List<String> provides = new LinkedList<>();
        String moduleNameExtention = "_client";

        desc.requires().stream().forEach((ModuleDescriptor.Requires req) -> requires.add(req.name()));
        desc.exports().stream().forEach((ModuleDescriptor.Exports exp)-> exports.add(((exp.isQualified()) ? exp.source()+" to" + exp.targets(): exp.source())));
        desc.provides().stream().forEach((ModuleDescriptor.Provides p) -> provides.add(p.toString().replace("]","").replace("[","")));

        //Füge export für das Server Module inzu, wird benötigt, dass ZMQServer auf die ServiceImpl Klasse zugreifen kann
        if(!exportPackageName.equals("")){
            moduleNameExtention = "_server";
            if(!exports.contains(exportPackageName))
                exports.add(exportPackageName + " to " + "libmodule");
        }

        //Ob CLient oder Server benötigen beide Module das proxygen Modul um auf die Helper zuzugreifen
        requires.add("libmodule");

        root.put("moduleName",desc.name()+moduleNameExtention);
        root.put("requires",requires);
        root.put("exports", exports);
        root.put("provides", provides);

        //lade Module-info Template
        Template temp = cfg.getTemplate("module-info.ftl");


        File moduleFile = new File(fileDest+"/module-info.java");
        moduleFile.getParentFile().mkdir();


        try(Writer osw = new OutputStreamWriter(new FileOutputStream(moduleFile))){
            temp.process(root,osw);
        }catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void skript(String projectDir, Set<String> mods) throws IOException {
        Map<String,Object> root = new HashMap<>();
        List<String> modules = new LinkedList<>();
        modules.addAll(mods);

        root.put("modules", modules);
        root.put("projDir",projectDir);

        Template temp = cfg.getTemplate("compile.ftl");

        File compileFile = new File(projectDir + File.separator + "generatedFiles" + File.separator +"skript.sh");

        try(Writer osw = new OutputStreamWriter(new FileOutputStream(compileFile))){
            temp.process(root,osw);
        }catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
