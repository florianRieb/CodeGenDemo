package com.gen;

import com.helper.ProxyClient;
import com.helper.ZMQServer;

import java.io.DataOutput;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.nio.file.Paths;
import java.util.Set;

public class TeschdMain {

    public static void main(String... args) throws Exception {
        String args0 = System.getProperty("user.dir");

        ModuleFinder finder = ModuleFinder.of(Paths.get(args0+ "/mlib"),Paths.get(args0 + "/generatedFiles/mods"));

        //Laden der Module und generieren des Source Codes
        ModuleLayer bootLayer = ModuleLayer.boot();
        Configuration config = bootLayer.configuration().resolve(finder, ModuleFinder.of(), Set.of("app"));

        finder.find("app").get().descriptor().requires().stream()
                .filter((ModuleDescriptor.Requires req)-> !req.name().startsWith("java."))
                .forEach((ModuleDescriptor.Requires req) -> System.out.println(finder.find(req.name()).get().location().get().getPath()));

        ClassLoader scl = ClassLoader.getSystemClassLoader();
        ModuleLayer newLayer = bootLayer.defineModulesWithOneLoader(config,scl);

    }
}
