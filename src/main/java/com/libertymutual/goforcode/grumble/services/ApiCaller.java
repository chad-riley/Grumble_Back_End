package com.libertymutual.goforcode.grumble.services;

import java.io.IOException;

import com.libertymutual.goforcode.grumble.models.ImageApiCredentials;
import com.libertymutual.goforcode.grumble.models.MenuItem;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

public class ApiCaller {
	
	private ImageApiCredentials apiCreds;
	private int index;
	private String cxKey;
	private String apiKey;
	private String eatStreetKey;
	
	public ApiCaller(ImageApiCredentials credentials) {
		this.apiCreds = credentials;
		this.index = 0;
		this.cxKey = apiCreds.getCxKey(index);
		this.apiKey = apiCreds.getApiKey(index);
		this.eatStreetKey = apiCreds.getEatStreetKey();
	}
	
	//Call EatStreet API to get list of restaurants based on city
	public JSONArray callApiToRetrieveRestaurants (String city, String pickup_radius) throws IOException, Exception {
		Resty r = new Resty();
		return (JSONArray) r
				.json("https://api.eatstreet.com/publicapi/v1/restaurant/search?method=both" + "&pickup-radius=" + pickup_radius + "&street-address=" + city
						+ "&access-token=" + this.eatStreetKey)
				.get("restaurants");
	} 
	
	//Call EatStreet API to get list of restaurants based on latitude and longitude
	public JSONArray callApiToRetrieveRestaurants(String latitude, String longitude, String pickup_radius) throws IOException, Exception {
		Resty r = new Resty();
		return (JSONArray) r
				.json("https://api.eatstreet.com/publicapi/v1/restaurant/search?latitude=" + latitude + "&longitude=" + longitude +
						"&method=both" + "&pickup-radius=" + pickup_radius + "&access-token=" + this.eatStreetKey)
				.get("restaurants");
	}
	
	//Call EatStreet API to get menu for specific restaurant
	public JSONArray callApiToRetrieveMenu(String oneRestaurantKey) throws IOException, JSONException {
		Resty r = new Resty();
		return r.json("https://api.eatstreet.com/publicapi/v1/restaurant/"
				+ oneRestaurantKey + "/menu?includeCustomizations=false&access-token=" + this.eatStreetKey).array();
	}
	
	//Call Google Custom Search API to get single picture URL for specific menu item
	public JSONResource callApiToRetrieveMenuItemPictureURL(MenuItem currentItem) throws IOException {
		Resty r = new Resty();
		JSONResource reply = new JSONResource();
		boolean foundAPic = false;
		
		String baseUrl = "https://www.googleapis.com/customsearch/v1?q=food+"
				     + encode(currentItem.getName());
		baseUrl = baseUrl.replaceAll(" ", "+");
		baseUrl = baseUrl.replaceAll("\"", "");
		baseUrl = baseUrl.replaceAll("`", "");
		
		while (!foundAPic) {
			try {
				String url = baseUrl + "&cx=" + this.cxKey + 
				      "&searchType=image&key=" + this.apiKey + 
				      "&num=1&fields=items%2Flink";
				reply = r.json(url);
				foundAPic = true;
			} catch (Exception e) {
				this.index = this.index + 1;
				try {
					this.cxKey = apiCreds.getCxKey(this.index);
					this.apiKey = apiCreds.getApiKey(this.index);
				} catch (IndexOutOfBoundsException ioobe) {
					return null;
				}
				
			}
		}
		return reply;
	}
	
	private String encode(String s) {
		return s.replaceAll("[\"`]", "");
	}
}