package com.libertymutual.goforcode.grumble.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
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
	
	private Random randomGenerator;
	private RestaurantRepository restaurantRepo;
	private MenuItemRepository declinedMenuItemRepo;
	private MenuItem currentItem;
	
	public RestaurantApiController(RestaurantRepository restaurantRepo, MenuItemRepository declinedMenuItemRepo) {
		randomGenerator = new Random();
		this.restaurantRepo = restaurantRepo;
		this.declinedMenuItemRepo = declinedMenuItemRepo;
	}
	
	@GetMapping("/{city}")
	public MenuItem newMenuItemRequest (@PathVariable String city) throws IOException, Exception {
		Resty r = new Resty();
		restaurantRepo.deleteAll();
		
		//Call EatStreet API to get list of restaurants in desired city
		JSONArray restaurantArray = (JSONArray) r.json("https://api.eatstreet.com/publicapi/v1/restaurant/search?method=both&street-address="
									+ city + "&access-token=44dbbeccae3c7537").get("restaurants");
		
		//Populate a list of Restaurants based on results of API 
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
		
		//Call get random index method to return integer based on size of restaurant list then populate single restaurant
		int index = getARandomIndex(restaurantList.size());
		Restaurant restaurant = restaurantList.get(index);
		String oneRestaurantKey = restaurant.getRestaurantApiKey();
		System.out.println(restaurantList.get(index).getRestaurantName());
		
		//Call API to retrieve menu which returns JSON array of menu sections
		JSONArray menuSections = callApiToRetrieveMenu(oneRestaurantKey);
		
		//Call generate menu item list method to fill our list of menu items
		List<MenuItem> menuItemList = generateMenuItemList(menuSections, restaurant);
		
		//Select random menu item and return it
		if (menuItemList.size() > 1) {
			index = getARandomIndex(menuItemList.size() - 1);
		} else {
			return newMenuItemRequest(city);
		}

		this.currentItem = menuItemList.get(index);
		System.out.println("Name of Item: " + menuItemList.get(index).getName());
		System.out.println("Name of Item: " + menuItemList.get(index).getBasePrice());
		System.out.println("Name of Item: " + menuItemList.get(index).getDescription());
		
		JSONResource menuImage = r.json( 
				"https://www.googleapis.com/customsearch/v1?q="+
				currentItem.getRestaurant().getRestaurantName().replaceAll(" ", "+")+"+"+currentItem.getName().replaceAll(" ", "+")+"+"+
				this.currentItem.getRestaurant().getCity().replaceAll(" ", "+")+"+"
				+"&cx=008419413090451472490:9ehq9f7q5q8&searchType=image&key=AIzaSyBR1FQBAgE0KiZLS2eYLuer3r992uxJUSo&num=1&fields=items%2Flink");
		
		try {
			JSONArray imageArray = (JSONArray) menuImage.get("items");
			this.currentItem.setImageURL(imageArray.getJSONObject(0).get("link").toString());
		} catch (JSONException je) {
			System.out.println("could not find photo");
			return newMenuItemRequest(city);
		}
		
		//return menuItemRepo.findOne((long) index);
		return this.currentItem;
	}
	
	@GetMapping("/item")
	public MenuItem getAnotherMenuItem() throws Exception {
		//Declare local variables
		int index = 0;
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		
		//Add declined item to our list of declined menu items
		this.declinedMenuItemRepo.save(this.currentItem);
		
		//Retrieve full list of available restaurants
		List<Restaurant> restaurantList = restaurantRepo.findAll();
		
		try {
			boolean weHaveAValidIndex = false;
			while (!weHaveAValidIndex) {
				//Using random generator to return random value based on size of restaurant list
				index = getARandomIndex(restaurantList.size());
				Restaurant restaurant = restaurantList.get(index);
				String oneRestaurantKey = restaurant.getRestaurantApiKey();
				System.out.println(restaurantList.get(index).getRestaurantName());
				
				//Call API to retrieve menu which returns JSON array of menu sections
				JSONArray menuSections = callApiToRetrieveMenu(oneRestaurantKey);
				
				//Call generate menu item list method to fill our list of menu items
				menuItemList = generateMenuItemList(menuSections, restaurant);
				
				//Select random menu item and return it
				if (menuItemList.size() > 1) {
					index = getARandomIndex(menuItemList.size() - 1);
					weHaveAValidIndex = true;
				} else {
					restaurantRepo.delete(restaurant);
					System.out.println("size check is working!");
				}
			}//end of while loop; should have index to retrieve single menu item at this point	
			
			this.currentItem = menuItemList.get(index);
			System.out.println("Name of Item: " + menuItemList.get(index).getName());
			System.out.println("Price: " + menuItemList.get(index).getBasePrice());
			System.out.println("Description: " + menuItemList.get(index).getDescription());
			
			//Call picture URL method to return JSONResource containing single link to a menu item photo
			JSONResource menuImage = callApiToRetrieveMenuItemPictureURL();
			
			try {
				JSONArray imageArray = (JSONArray) menuImage.get("items");
				this.currentItem.setImageURL(imageArray.getJSONObject(0).get("link").toString());
			} catch (JSONException je) {
				System.out.println("could not find photo");
				return getAnotherMenuItem();
			}
			
			
			System.out.println(this.currentItem.getImageURL());
			return this.currentItem;
			
		} catch (IOException ioe) {
			System.out.println("exception check is working!");
			return getAnotherMenuItem();
		}
	}
	
	
	//Takes a size parameter and returns a random integer within that range
	private int getARandomIndex(int size) {
		return randomGenerator.nextInt(size);
	}
	
	//Call EatStreet API to get menu for desired restaurant
	private JSONArray callApiToRetrieveMenu(String oneRestaurantKey) throws IOException, JSONException {
		Resty r = new Resty();
		return r.json("https://api.eatstreet.com/publicapi/v1/restaurant/"
				+ oneRestaurantKey + "/menu?includeCustomizations=false&access-token=44dbbeccae3c7537").array();
	}
	
	//Call Google Custom Search API to get single picture URL for specific menu item
	private JSONResource callApiToRetrieveMenuItemPictureURL() throws IOException {
		Resty r = new Resty();
		return r.json( 
				"https://www.googleapis.com/customsearch/v1?q="+
				currentItem.getRestaurant().getRestaurantName().replaceAll(" ", "+")+"+"+currentItem.getName().replaceAll(" ", "+")+"+"+
				this.currentItem.getRestaurant().getCity().replaceAll(" ", "+")+"+"
				+"&cx=008419413090451472490:9ehq9f7q5q8&searchType=image&key=AIzaSyBR1FQBAgE0KiZLS2eYLuer3r992uxJUSo&num=1&fields=items%2Flink");
	}
	
	//Populate a list of menu items based on results of API, item added if it contains description and costs more than $3
	private List<MenuItem> generateMenuItemList(JSONArray menuSections, Restaurant restaurant) throws IOException, JSONException {
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		for (int i = 0; i < menuSections.length(); i++) {
			for (int j = 0; j < menuSections.getJSONObject(i).getJSONArray("items").length(); j++) {
				MenuItem oneItem = new MenuItem();
				oneItem.setName(menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).get("name").toString());
				oneItem.setBasePrice(menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).get("basePrice").toString());
				oneItem.setRestaurant(restaurant);
				if (menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).toString().contains("description")
					&& Double.parseDouble(oneItem.getBasePrice()) > 3.00) {
					oneItem.setDescription(menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).get("description").toString());
					menuItemList.add(oneItem);
				}
			}			
		}
		return menuItemList;
	}

}
