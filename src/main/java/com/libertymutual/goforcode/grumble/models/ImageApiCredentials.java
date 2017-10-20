package com.libertymutual.goforcode.grumble.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ImageApiCredentials {
	private List<String> cxKey;
	private List<String> apiKey;
	private String eatStreetKey;
	
	public ImageApiCredentials (@Value("${secret.secret.keys}") String listOfKeysOriginallyCalledFromEnvironmentVariables, 
								@Value("${marginally.less.secret.key}") String eatStreetKeyComingFromEnvVariables) {
		
		this.cxKey = new ArrayList<String>();
		this.apiKey = new ArrayList<String>();
		this.eatStreetKey = eatStreetKeyComingFromEnvVariables;
		
		String[] pairs = listOfKeysOriginallyCalledFromEnvironmentVariables.split("\\|");
		for (String pair : pairs) {
			String[] keys = pair.split(",");
			this.cxKey.add(keys[0]);
			this.apiKey.add(keys[1]);
		}
	}
	
	public String getCxKey(int index) {
		return this.cxKey.get(index);
	}
	
	public String getApiKey(int index) {
		return this.apiKey.get(index);
	}

	public String getEatStreetKey() {
		return this.eatStreetKey;
	}

}
