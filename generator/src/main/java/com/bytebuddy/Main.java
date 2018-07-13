package com.bytebuddy;

public class Main {
    public static void main(String... args){

        System.out.println(Main.class.getName().replace(".","/"));


        BuddyGenerator gen = new BuddyGenerator();
        //gen.doStuffAndSave();
    }
}
