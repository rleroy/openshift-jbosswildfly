package com.leroy.ronan.utils.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.log4j.Logger;

public class FileUtils {
    
    private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

    public static String read(File f){
        String res = null;
        try {
            res = new String(Files.readAllBytes(f.toPath()), "utf-8");
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return res;
    }
    
    public static void write(File f, String s){
        try {
            f.createNewFile();
            Files.write(f.toPath(), s.getBytes("utf-8"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
