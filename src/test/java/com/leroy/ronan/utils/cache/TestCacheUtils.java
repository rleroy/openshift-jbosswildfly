package com.leroy.ronan.utils.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.log4j.Logger;

public class TestCacheUtils {
    
    public static final String KEY   = "key";
    public static final String FRESH = "FRESH";
    public static final String OLD   = "OLD";
    
    private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

    public static boolean isExpired(String content, long time) {
        return !FRESH.equals(content);
    }
    
    public static File keyToFile(String key){
       return new File("cache-tmp/test/key.tmp");
    }

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
            f.delete();
            if (s != null) {
                f.delete();
                f.createNewFile();
                Files.write(f.toPath(), s.getBytes("utf-8"));
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

}
