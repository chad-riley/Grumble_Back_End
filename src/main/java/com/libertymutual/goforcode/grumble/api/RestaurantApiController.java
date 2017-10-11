package com.libertymutual.goforcode.grumble.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

import com.libertymutual.goforcode.grumble.models.MenuItem;
import com.libertymutual.goforcode.grumble.models.Restaurant;
import com.libertymutual.goforcode.grumble.services.MenuItemRepository;
import com.libertymutual.goforcode.grumble.services.RestaurantRepository;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping ("/api")
public class RestaurantApiController {
	
	//used to get random index
	private Random randomGenerator;
	private RestaurantRepository restaurantRepo;
	private MenuItemRepository menuItemRepo;
	
	public RestaurantApiController(RestaurantRepository restaurantRepo, MenuItemRepository menuItemRepo) {
		randomGenerator = new Random();
		this.restaurantRepo = restaurantRepo;
		this.menuItemRepo = menuItemRepo;
	}
	
	@GetMapping("/{city}")
	public MenuItem newMenuItemRequest (@PathVariable String city) throws IOException, Exception {
		Resty r = new Resty();
		
		//Call EatStreet API to get list of restaurants in desired city
		JSONArray restaurantArray = (JSONArray) r.json("https://api.eatstreet.com/publicapi/v1/restaurant/search?method=both&street-address="
									+ city + "&access-token=44dbbeccae3c7537").get("restaurants");
		
		//Populate a list of Restaurants based on results of API 
		List<Restaurant> restaurantList = new ArrayList<Restaurant>();
		for (int i = 0; i < restaurantArray.length(); i++) {
			Restaurant oneRestaurant = new Restaurant();
			oneRestaurant.setRestaurantApiKey(restaurantArray.getJSONObject(i).getString("apiKey"));
//			System.out.println(oneRestaurant.getRestaurantApiKey());
			oneRestaurant.setRestaurantName(restaurantArray.getJSONObject(i).getString("name"));
			oneRestaurant.setLatitude(restaurantArray.getJSONObject(i).getString("latitude"));
			oneRestaurant.setLongitude(restaurantArray.getJSONObject(i).getString("longitude"));
//			System.out.println(oneRestaurant.getRestaurantName());
			restaurantList.add(oneRestaurant);
			restaurantRepo.save(oneRestaurant);
		}
		
		//Using random generator to return random value based on size of restaurant list
		int index = randomGenerator.nextInt(restaurantList.size());
		String oneRestaurantKey = restaurantList.get(index).getRestaurantApiKey();
		Restaurant restaurant = restaurantList.get(index);
		String restaurantName = restaurantList.get(index).getRestaurantName();
		System.out.println(restaurantList.get(index).getRestaurantName());
		
		//Call EatStreet API to get menu for desired restaurant
		JSONArray menuSections = r.json("https://api.eatstreet.com/publicapi/v1/restaurant/"
									+ oneRestaurantKey + "/menu?includeCustomizations=false&access-token=44dbbeccae3c7537").array();
		
		//Populate a list of menu items based on results of API, item added if it contains description and costs more than $3
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		for (int i = 0; i < menuSections.length(); i++) {
			for (int j = 0; j < menuSections.getJSONObject(i).getJSONArray("items").length(); j++) {
				MenuItem oneItem = new MenuItem();
				oneItem.setName(menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).get("name").toString());
				oneItem.setBasePrice(menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).get("basePrice").toString());
				if (menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).toString().contains("description")
					&& Double.parseDouble(oneItem.getBasePrice()) > 3.00) {
					oneItem.setDescription(menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).get("description").toString());
					menuItemList.add(oneItem);
					menuItemRepo.save(oneItem);
				}
			}			
		}
		
		//Select random menu item and return it
		index = randomGenerator.nextInt(menuItemList.size());
		System.out.println("Name of Item: " + menuItemList.get(index).getName());
		System.out.println("Name of Item: " + menuItemList.get(index).getBasePrice());
		System.out.println("Name of Item: " + menuItemList.get(index).getDescription());
		//return menuItemRepo.findOne((long) index);
		return menuItemList.get(index);
	}

}
