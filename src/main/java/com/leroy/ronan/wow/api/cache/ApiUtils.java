package com.leroy.ronan.wow.api.cache;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;

import org.apache.log4j.Logger;

import com.leroy.ronan.wow.beans.WowJson;

public class ApiUtils {

	private static final Logger log = Logger.getLogger(new Object() { }.getClass().getEnclosingClass());

    protected static String fromFile(File f){
    	String res = null;
    	try {
			res = new String(Files.readAllBytes(f.toPath()), "utf-8");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
    	return res;
    }

    protected static void toFile(File f, WowJson t){
		f.getParentFile().mkdirs();
		if (f.exists()){
			f.delete();
		}
		if (t != null){
			try {
				f.createNewFile();
				Files.write(f.toPath(), t.getJson().getBytes("utf-8"));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
    }
    
    protected static String loadUri(URI uri){
        String data = null;
		try {
			URL url = uri.toURL();
			URLConnection conn = url.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			data = "";
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
			    data += inputLine;
			}
			br.close();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
        return data;
    }

}
