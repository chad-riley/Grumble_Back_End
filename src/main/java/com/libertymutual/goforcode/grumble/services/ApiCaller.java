package com.libertymutual.goforcode.grumble.services;

import java.io.IOException;

import com.libertymutual.goforcode.grumble.models.MenuItem;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

public class ApiCaller {
	
	//Call EatStreet API to get list of restaurants based on city
	public JSONArray callApiToRetrieveRestaurants (String city) throws IOException, Exception {
		Resty r = new Resty();
		return (JSONArray) r
				.json("https://api.eatstreet.com/publicapi/v1/restaurant/search?method=both&street-address=" + city
						+ "&access-token=44dbbeccae3c7537")
				.get("restaurants");
	}
	
	//Call EatStreet API to get list of restaurants based on lat and long
	public JSONArray callApiToRetrieveRestaurants(String latitude, String longitude) throws IOException, Exception {
		Resty r = new Resty();
		return (JSONArray) r
				.json("https://api.eatstreet.com/publicapi/v1/restaurant/search?latitude=" + latitude + "&longitude=" + longitude +
						"&method=both&access-token=44dbbeccae3c7537")
				.get("restaurants");
	}
	
	//Call EatStreet API to get menu for specific restaurant
	public JSONArray callApiToRetrieveMenu(String oneRestaurantKey) throws IOException, JSONException {
		Resty r = new Resty();
		return r.json("https://api.eatstreet.com/publicapi/v1/restaurant/"
				+ oneRestaurantKey + "/menu?includeCustomizations=false&access-token=44dbbeccae3c7537").array();
	}
	
	//Call Google Custom Search API to get single picture URL for specific menu item
	public JSONResource callApiToRetrieveMenuItemPictureURL(MenuItem currentItem) throws IOException {
		Resty r = new Resty();
		String url = "https://www.googleapis.com/customsearch/v1?q=food+"
//				+ currentItem.getRestaurant().getRestaurantName().replaceAll(" ", "+") + "+"
			+ currentItem.getName() + "+"
//				+ this.currentItem.getRestaurant().getCity().replaceAll(" ", "+") + "+"
			+ "&cx=002392119250457641008:zovcx9rlbaw&searchType=image&key=AIzaSyCPEZNXOBI9ZfcEzcEZfDjexTysIHeaScU&num=1&fields=items%2Flink";
		url = url.replaceAll(" ", "+");
		url = url.replaceAll("\"", "");
		url = url.replaceAll("`", "");
		return r.json(url);
	}
}