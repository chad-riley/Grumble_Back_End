package com.libertymutual.goforcode.grumble.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.web.JSONResource;

import com.libertymutual.goforcode.grumble.models.MenuItem;
import com.libertymutual.goforcode.grumble.models.Restaurant;
import com.libertymutual.goforcode.grumble.services.MenuItemRepository;
import com.libertymutual.goforcode.grumble.services.RestaurantRepository;
import com.libertymutual.goforcode.grumble.services.ApiCaller;
import com.libertymutual.goforcode.grumble.services.ListFiller;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class RestaurantApiController {

	private Random randomGenerator;
	private RestaurantRepository restaurantRepo;
	private MenuItemRepository declinedMenuItemRepo;
	private MenuItem currentItem;
	private ApiCaller apiCaller;
	private ListFiller listFiller;
	private MenuItem nothingFound;

	public RestaurantApiController(RestaurantRepository restaurantRepo, MenuItemRepository declinedMenuItemRepo) {
		this.randomGenerator = new Random();
		this.restaurantRepo = restaurantRepo;
		this.declinedMenuItemRepo = declinedMenuItemRepo;
		this.apiCaller = new ApiCaller();
		this.listFiller = new ListFiller();
		
		this.nothingFound = new MenuItem();
		this.nothingFound.setName("No results found in that location");
		this.nothingFound.setDescription("Please try a new location");
		this.nothingFound.setImageURL("https://media.giphy.com/media/forX81kqyzD4A/giphy.gif");
	}

	@GetMapping("/{city}")
	public MenuItem newMenuItemRequest(@PathVariable String city) throws IOException, Exception {
		int index = 0;
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		JSONResource menuImage = new JSONResource();
		
		restaurantRepo.deleteAll();

		JSONArray restaurantArray = new JSONArray();
		try {
			restaurantArray = apiCaller.callApiToRetrieveRestaurants(city);
		} catch (IOException ioe) {
			return this.nothingFound;
		}
		
		List<Restaurant> restaurantList = listFiller.fillMyListOfRestaurants(restaurantArray, restaurantRepo);

		boolean weHaveAValidIndex = false;
		boolean weHaveAValidPhoto = false;
		while (!weHaveAValidIndex || !weHaveAValidPhoto) {
			//Using random generator to return random value based on size of restaurant list
			if (restaurantList.size() > 0) index = getARandomIndex(restaurantList.size());
			else return this.nothingFound;
			
			Restaurant restaurant = restaurantList.get(index);
			String oneRestaurantKey = restaurant.getRestaurantApiKey();
			System.out.println(restaurant.getRestaurantName());
			
			try {
				//Call API to retrieve menu which returns JSON array of menu sections
				JSONArray menuSections = apiCaller.callApiToRetrieveMenu(oneRestaurantKey);
				
				//Call generate menu item list method to fill our list of menu items
				menuItemList = listFiller.fillMyMenuItemList(menuSections, restaurant, declinedMenuItemRepo);
				
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
	
	@GetMapping("/{latitude}/{longitude}/")
	public MenuItem newMenuItemRequestWithLatitudeAndLongitude(@PathVariable String latitude, @PathVariable String longitude) throws IOException, Exception {
		int index = 0;
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		JSONResource menuImage = new JSONResource();
		
		restaurantRepo.deleteAll();

		JSONArray restaurantArray = new JSONArray();
		try {
			restaurantArray = apiCaller.callApiToRetrieveRestaurants(latitude, longitude);
		} catch (IOException ioe) {
			return this.nothingFound;
		}
		System.out.println("passing the try/catch");
		List<Restaurant> restaurantList = listFiller.fillMyListOfRestaurants(restaurantArray, restaurantRepo);
		System.out.println(restaurantList.size());
		boolean weHaveAValidIndex = false;
		boolean weHaveAValidPhoto = false;
		while (!weHaveAValidIndex || !weHaveAValidPhoto) {
			//Using random generator to return random value based on size of restaurant list
			if (restaurantList.size() > 0) index = getARandomIndex(restaurantList.size());
			else return this.nothingFound;
			
			Restaurant restaurant = restaurantList.get(index);
			String oneRestaurantKey = restaurant.getRestaurantApiKey();
			System.out.println(restaurant.getRestaurantName());
			
			try {
				//Call API to retrieve menu which returns JSON array of menu sections
				JSONArray menuSections = apiCaller.callApiToRetrieveMenu(oneRestaurantKey);
				
				//Call generate menu item list method to fill our list of menu items
				menuItemList = listFiller.fillMyMenuItemList(menuSections, restaurant, declinedMenuItemRepo);
				
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
				menuItemList = listFiller.fillMyMenuItemList(menuSections, restaurant, declinedMenuItemRepo);
				
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
}
