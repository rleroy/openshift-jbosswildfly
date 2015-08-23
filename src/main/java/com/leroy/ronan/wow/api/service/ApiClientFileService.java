package com.leroy.ronan.wow.api.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.log4j.Logger;

import com.leroy.ronan.wow.api.ApiResponse;
import com.leroy.ronan.wow.api.ApiType;

public class ApiClientFileService extends ApiClientService{

    private final static Logger logger = Logger.getLogger(ApiClientFileService.class);

	private String root;
	private LocalDateTime minBirth;
	private Duration maxAge;

	public ApiClientFileService(String root, LocalDateTime minBirth, Duration maxAge) {
		super();
		this.root = root;
		this.minBirth = minBirth;
		this.maxAge = maxAge;
	}
	
	@Override
	public boolean isAvailable(String zone, ApiType type, String realm, String name) {
		String filePath = buildFilePath(zone, type, realm, name);
		File f = new File(filePath);
		return f.exists() && isValid(f);
	}

	@Override
	protected ApiResponse getDataInternal(String zone, ApiType type, String realm, String name) {
		ApiResponse res = null;
		if (isAvailable(zone, type, realm, name)){
			try {
				res = new ApiResponse(new String(Files.readAllBytes(Paths.get(buildFilePath(zone, type, realm, name))), "utf-8"));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return res;
	}

	@Override
	public void putData(String zone, ApiType type, String realm, String name, String json) {
		String filePath = buildFilePath(zone, type, realm, name);
		File f = new File(filePath);
		f.getParentFile().mkdirs();
		if (f.exists()){
			f.delete();
		}
		if (json != null){
			try {
				f.createNewFile();
				Files.write(Paths.get(filePath), json.getBytes("utf-8"));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	private String buildFilePath(String zone, ApiType type, String realm, String name){
		return root+"/"+type.name()+"/"+realm+"/"+name+".json";
	}

	private boolean isValid(File f) {
		boolean res = false;
		if (f.exists()){
			LocalDateTime lastModif = LocalDateTime.ofInstant(Instant.ofEpochMilli(f.lastModified()), ZoneId.systemDefault());
			if (minBirth.isBefore(lastModif)){
				if (Duration.between(lastModif, LocalDateTime.now()).compareTo(maxAge) < 0){
					res = true;
				}
			}
		}
		return res;
	}



}
