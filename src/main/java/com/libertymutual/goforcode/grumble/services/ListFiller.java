package com.libertymutual.goforcode.grumble.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.libertymutual.goforcode.grumble.models.MenuItem;
import com.libertymutual.goforcode.grumble.models.Restaurant;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;

public class ListFiller {
	
	// Populate a list of restaurants based on the results of the restaurant array 
	// returned by EatStreet API
	public List<Restaurant> fillMyListOfRestaurants(JSONArray restaurantArray, RestaurantRepository restaurantRepo) throws JSONException {
		List<Restaurant> restaurantList = new ArrayList<Restaurant>();
		for (int i = 0; i < restaurantArray.length(); i++) {
			Restaurant oneRestaurant = new Restaurant();
			oneRestaurant.setRestaurantApiKey(restaurantArray.getJSONObject(i).getString("apiKey"));
			oneRestaurant.setRestaurantName(restaurantArray.getJSONObject(i).getString("name"));
			oneRestaurant.setLatitude(restaurantArray.getJSONObject(i).getString("latitude"));
			oneRestaurant.setLongitude(restaurantArray.getJSONObject(i).getString("longitude"));
			oneRestaurant.setAddress(restaurantArray.getJSONObject(i).getString("streetAddress"));
			oneRestaurant.setCity(restaurantArray.getJSONObject(i).getString("city"));
			oneRestaurant.setState(restaurantArray.getJSONObject(i).getString("state"));
			oneRestaurant.setZip(restaurantArray.getJSONObject(i).getString("zip"));
			oneRestaurant.setPhone(restaurantArray.getJSONObject(i).getString("phone"));
			restaurantList.add(oneRestaurant);
			restaurantRepo.save(oneRestaurant);
		}
		return restaurantList;
	}
	
	// Populate a list of menu items based on results of API, item added if it
	// contains description and costs more than $3, exclude items that contain 
	// the text party or catering
	public List<MenuItem> fillMyMenuItemList(JSONArray menuSections, Restaurant restaurant, MenuItemRepository declinedMenuItemRepo) throws IOException, JSONException {
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		for (int i = 0; i < menuSections.length(); i++) {
			for (int j = 0; j < menuSections.getJSONObject(i).getJSONArray("items").length(); j++) {
				MenuItem oneItem = new MenuItem();
				oneItem.setName(
						menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).get("name").toString());
				oneItem.setBasePrice(menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j)
						.get("basePrice").toString());
				oneItem.setRestaurant(restaurant);
				if (menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).toString().contains("description") 
					&& Double.parseDouble(oneItem.getBasePrice()) > 4.00
					&& !oneItem.getName().toLowerCase().contains("party")
					&& !oneItem.getName().toLowerCase().contains("catering")
					&& declinedMenuItemRepo.findByNameContaining(oneItem.getName()).size() == 0) 
				{
					oneItem.setDescription(menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j)
							.get("description").toString());
					menuItemList.add(oneItem);
				}
			}
		}
		return menuItemList;
	}

	
}
