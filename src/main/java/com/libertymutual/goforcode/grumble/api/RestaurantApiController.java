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

	@GetMapping("/{city}/{pickup_radius}/")
	public MenuItem newMenuItemRequest(@PathVariable String city, @PathVariable String pickup_radius) throws IOException, Exception {
		restaurantRepo.deleteAll();

		JSONArray restaurantArray = new JSONArray();
		try {
			restaurantArray = apiCaller.callApiToRetrieveRestaurants(city, pickup_radius);
		} catch (IOException ioe) {
			return this.nothingFound;
		}		
		List<Restaurant> restaurantList = listFiller.fillMyListOfRestaurants(restaurantArray, restaurantRepo);
		
		this.currentItem = getASingleMenuItem(restaurantList);
		
		return this.currentItem;
	}
	
	@GetMapping("/{latitude}/{longitude}/{pickup_radius}/")
	public MenuItem newMenuItemRequestWithLatitudeAndLongitude(@PathVariable String latitude, @PathVariable String longitude, @PathVariable String pickup_radius) throws IOException, Exception {
		restaurantRepo.deleteAll();

		JSONArray restaurantArray = new JSONArray();
		try {
			restaurantArray = apiCaller.callApiToRetrieveRestaurants(latitude, longitude, pickup_radius);
		} catch (IOException ioe) {
			return this.nothingFound;
		}
		List<Restaurant> restaurantList = listFiller.fillMyListOfRestaurants(restaurantArray, restaurantRepo);
		
		this.currentItem = getASingleMenuItem(restaurantList);
		
		return this.currentItem;
	}

	@GetMapping("/item")
	public MenuItem getAnotherMenuItem() throws Exception {
		this.declinedMenuItemRepo.save(this.currentItem);
		
		List<Restaurant> restaurantList = restaurantRepo.findAll();
		
		this.currentItem = getASingleMenuItem(restaurantList);
		
		return this.currentItem;
	}

	// Takes a size parameter and returns a random integer within that range
	private int getARandomIndex(int size) {
		return randomGenerator.nextInt(size);
	}
	
	//Takes a list of restaurants and returns a single menu item 
	private MenuItem getASingleMenuItem(List<Restaurant> restaurantList) throws Exception {
		int index = 0;
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		JSONResource menuImage = new JSONResource();
		
		//Generate a valid index for a single menu item and a valid photo for that item
		//Create two boolean indicators and run while loop if either indicator is still false
		boolean weHaveAValidIndex = false;
		boolean weHaveAValidPhoto = false;
		while (!weHaveAValidIndex || !weHaveAValidPhoto) {
			//Using random generator to return random index value based on size of restaurant list
			//Otherwise return default nothing found menu item
			if (restaurantList.size() > 0) index = getARandomIndex(restaurantList.size());
			else return this.nothingFound;
			
			//Get single restaurant using random index and populate restaurant key
			Restaurant restaurant = restaurantList.get(index);
			String oneRestaurantKey = restaurant.getRestaurantApiKey();
			
			try {
				//Call API to retrieve menu which returns JSON array of menu sections for that restaurant
				JSONArray menuSections = apiCaller.callApiToRetrieveMenu(oneRestaurantKey);
				
				//Call generate menu item list method to fill our list of menu items
				menuItemList = listFiller.fillMyMenuItemList(menuSections, restaurant, declinedMenuItemRepo);
				
				//Using random generator to return random index value based on size of menu item list
				//Select random menu item and store it in current item, set valid index indicator to true
				if (menuItemList.size() > 1) {
					index = getARandomIndex(menuItemList.size() - 1);
					this.currentItem = menuItemList.get(index);
					weHaveAValidIndex = true;
				//If menu item list size is below threshold, remove restaurant from repository and while 
				//loop will run again to get new restaurant
				} else {
					restaurantRepo.delete(restaurant);
					System.out.println("Exception: size of menu item list");
				} 
			} catch (IOException ioe) {
					System.out.println("Exception: input/output from API calls");
			}				
				
			//Use API caller to get an image JSON resource for single menu item
			menuImage = apiCaller.callApiToRetrieveMenuItemPictureURL(this.currentItem);
			
			//Pull single image link from JSON response and set it on current item
			//Switch valid photo indicator to true only when image is found
			try {
				JSONArray imageArray = (JSONArray) menuImage.get("items");
				this.currentItem.setImageURL(imageArray.getJSONObject(0).get("link").toString());
				if (currentItem.getImageURL() != null) weHaveAValidPhoto = true;
			} catch (JSONException je) {
				System.out.println("Exception: could not find photo");
				this.declinedMenuItemRepo.save(this.currentItem);
				weHaveAValidPhoto = false;
			}
		}//End of while loop; should have single menu item with photo at this point
		return this.currentItem;
	}
}
