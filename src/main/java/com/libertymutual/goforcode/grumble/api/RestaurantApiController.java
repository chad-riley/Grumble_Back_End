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

import com.libertymutual.goforcode.grumble.models.MenuItem;
import com.libertymutual.goforcode.grumble.models.Restaurant;
import com.libertymutual.goforcode.grumble.services.MenuItemRepository;
import com.libertymutual.goforcode.grumble.services.RestaurantRepository;
import com.libertymutual.goforcode.grumble.services.ApiCaller;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class RestaurantApiController {

	private Random randomGenerator;
	private RestaurantRepository restaurantRepo;
	private MenuItemRepository declinedMenuItemRepo;
	private MenuItem currentItem;
	private ApiCaller apiCaller;

	public RestaurantApiController(RestaurantRepository restaurantRepo, MenuItemRepository declinedMenuItemRepo) {
		randomGenerator = new Random();
		this.restaurantRepo = restaurantRepo;
		this.declinedMenuItemRepo = declinedMenuItemRepo;
		this.apiCaller = new ApiCaller();
	}

	@GetMapping("/{city}")
	public MenuItem newMenuItemRequest(@PathVariable String city) throws IOException, Exception {
		//Declare local variables
		int index = 0;
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		JSONResource menuImage = new JSONResource();
		
		restaurantRepo.deleteAll();

		// Call EatStreet API to get list of restaurants in desired city
		JSONArray restaurantArray = apiCaller.callApiToRetrieveRestaurants(city);

		// Populate a list of Restaurants based on results of API
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

		boolean weHaveAValidIndex = false;
		boolean weHaveAValidPhoto = false;
		while (!weHaveAValidIndex || !weHaveAValidPhoto) {
			//Using random generator to return random value based on size of restaurant list
			index = getARandomIndex(restaurantList.size());
			Restaurant restaurant = restaurantList.get(index);
			String oneRestaurantKey = restaurant.getRestaurantApiKey();
			System.out.println(restaurant.getRestaurantName());
			
			try {
				//Call API to retrieve menu which returns JSON array of menu sections
				JSONArray menuSections = apiCaller.callApiToRetrieveMenu(oneRestaurantKey);
				
				//Call generate menu item list method to fill our list of menu items
				menuItemList = generateMenuItemList(menuSections, restaurant);
				
				//Select random menu item and return it
				if (menuItemList.size() > 1) {
					index = getARandomIndex(menuItemList.size() - 1);
					this.currentItem = menuItemList.get(index);
					System.out.println("Name of Item: " + menuItemList.get(index).getName());
					System.out.println("Price: " + menuItemList.get(index).getBasePrice());
					System.out.println("Description: " + menuItemList.get(index).getDescription());
					weHaveAValidIndex = true;
				} else {
					restaurantRepo.delete(restaurant);
					System.out.println("Exception: size of menu item list");
				} 
			} catch (IOException ioe) {
					System.out.println("Exception: input/output from API calls");
			}				
				
			menuImage = apiCaller.callApiToRetrieveMenuItemPictureURL(this.currentItem);
			
			try {
				JSONArray imageArray = (JSONArray) menuImage.get("items");
				this.currentItem.setImageURL(imageArray.getJSONObject(0).get("link").toString());
				if (currentItem.getImageURL() != null) weHaveAValidPhoto = true;
			} catch (JSONException je) {
				System.out.println("Exception: could not find photo");
				this.declinedMenuItemRepo.save(this.currentItem);
				weHaveAValidPhoto = false;
			}
		}// end of while loop; should have single menu item with photo at this point
	return this.currentItem;
	}

	@GetMapping("/item")
	public MenuItem getAnotherMenuItem() throws Exception {
		//Declare local variables
		int index = 0;
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		JSONResource menuImage = new JSONResource();
		
		//Add declined item to our list of declined menu items
		this.declinedMenuItemRepo.save(this.currentItem);
		
		//Retrieve full list of available restaurants
		List<Restaurant> restaurantList = restaurantRepo.findAll();
		
		boolean weHaveAValidIndex = false;
		boolean weHaveAValidPhoto = false;
		while (!weHaveAValidIndex || !weHaveAValidPhoto) {
			//Using random generator to return random value based on size of restaurant list
			index = getARandomIndex(restaurantList.size());
			Restaurant restaurant = restaurantList.get(index);
			String oneRestaurantKey = restaurant.getRestaurantApiKey();
			System.out.println(restaurantList.get(index).getRestaurantName());
			
			try {
				//Call API to retrieve menu which returns JSON array of menu sections
				JSONArray menuSections = apiCaller.callApiToRetrieveMenu(oneRestaurantKey);
				
				//Call generate menu item list method to fill our list of menu items
				menuItemList = generateMenuItemList(menuSections, restaurant);
				
				//Select random menu item and return it
				if (menuItemList.size() > 1) {
					index = getARandomIndex(menuItemList.size() - 1);
					this.currentItem = menuItemList.get(index);
					System.out.println("Name of Item: " + menuItemList.get(index).getName());
					System.out.println("Price: " + menuItemList.get(index).getBasePrice());
					System.out.println("Description: " + menuItemList.get(index).getDescription());
					weHaveAValidIndex = true;
				} else {
					restaurantRepo.delete(restaurant);
					System.out.println("Exception: size of menu item list");
				} 
			} catch (IOException ioe) {
					System.out.println("Exception: input/output from API calls");
			}
				
			menuImage = apiCaller.callApiToRetrieveMenuItemPictureURL(this.currentItem);
			
			try {
				JSONArray imageArray = (JSONArray) menuImage.get("items");
				this.currentItem.setImageURL(imageArray.getJSONObject(0).get("link").toString());
				if (currentItem.getImageURL() != null) weHaveAValidPhoto = true;				
			} catch (JSONException je) {
				System.out.println("Exception: could not find photo");
				this.declinedMenuItemRepo.save(this.currentItem);
				weHaveAValidPhoto = false;
			}
		}// end of while loop; should have single menu item with photo at this point
	return this.currentItem;
	}

	// Takes a size parameter and returns a random integer within that range
	private int getARandomIndex(int size) {
		return randomGenerator.nextInt(size);
	}

	// Populate a list of menu items based on results of API, item added if it
	// contains description and costs more than $3
	private List<MenuItem> generateMenuItemList(JSONArray menuSections, Restaurant restaurant)
			throws IOException, JSONException {
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		for (int i = 0; i < menuSections.length(); i++) {
			for (int j = 0; j < menuSections.getJSONObject(i).getJSONArray("items").length(); j++) {
				MenuItem oneItem = new MenuItem();
				oneItem.setName(
						menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).get("name").toString());
				oneItem.setBasePrice(menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j)
						.get("basePrice").toString());
				oneItem.setRestaurant(restaurant);
				if (menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j).toString()
						.contains("description") && Double.parseDouble(oneItem.getBasePrice()) > 3.00) {
					oneItem.setDescription(menuSections.getJSONObject(i).getJSONArray("items").getJSONObject(j)
							.get("description").toString());
					menuItemList.add(oneItem);
				}
			}
		}
		return menuItemList;
	}

}
