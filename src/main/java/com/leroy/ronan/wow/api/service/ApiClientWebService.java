package com.leroy.ronan.wow.api.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.leroy.ronan.wow.api.ApiResponse;
import com.leroy.ronan.wow.api.ApiType;

public class ApiClientWebService extends ApiClientService{

    private final static Logger logger = Logger.getLogger(ApiClientWebService.class);

    private String locale;
    private String apiKey;
    
	public ApiClientWebService(String locale, String apiKey) {
		super();
		this.locale = locale;
		this.apiKey = apiKey;
	}

	@Override
	public boolean isAvailable(String zone, ApiType type, String realm, String name) {
		// Data is always available from the API.
		return true;
	}

	@Override
	protected ApiResponse getDataInternal(String zone, ApiType type, String realm, String name) {
        String path = "/wow/"+type+"/"+realm+"/"+name;
        List<String> optionsList = new ArrayList<>();
        optionsList.add("locale="+locale);
        optionsList.add("apikey="+apiKey);
        if (type.getFields() != null && type.getFields().size() > 0){
            optionsList.addAll(type.getFields().stream().map(s -> "fields=" + s).collect(Collectors.toList()));
        }
        String options = String.join("&", optionsList); 
        String data = null;
		try {
			URI uri = new URI("https", zone+".api.battle.net", path, options, null);
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
			logger.error(e.getMessage(), e);
		}
        return new ApiResponse(data);
	}

	@Override
	public void putData(String zone, ApiType type, String realm, String name, String json) {
		// Do nothing.
		logger.error("Cannot put data with this service!");
	}

}
