package com.libertymutual.goforcode.grumble.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.libertymutual.goforcode.grumble.models.MenuItem;
import com.libertymutual.goforcode.grumble.models.Restaurant;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.web.JSONResource;

public class MenuItemFinder {
	private Random randomGenerator;
	private ApiCaller apiCaller;
	private ListFiller listFiller;
	private MenuItem currentItem;
	private MenuItem nothingFound;
	
	public MenuItemFinder() {
		this.randomGenerator = new Random();
		this.apiCaller = new ApiCaller();
		this.listFiller = new ListFiller();
		this.nothingFound = new MenuItem();
		this.nothingFound.setName("No results found in that location");
		this.nothingFound.setDescription("Please try a new location");
		this.nothingFound.setImageURL("https://media.giphy.com/media/forX81kqyzD4A/giphy.gif");
	}
	
	//Takes a list of restaurants and returns a single menu item 
	public MenuItem getASingleMenuItem(MenuItemRepository declinedMenuItemRepo, RestaurantRepository restaurantRepo, String key) 
		    throws Exception {
		int index = 0;
		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
		JSONResource menuImage = new JSONResource();
		
		//Generate a valid index for a single menu item and a valid photo for that item
		//Create two boolean indicators and run while loop if either indicator is still false
		boolean weHaveAValidIndex = false;
		boolean weHaveAValidPhoto = false;
		while (!weHaveAValidIndex || !weHaveAValidPhoto) {
			List<Restaurant> thisSessionsRestaurants = restaurantRepo.findAllBySessionKey(key);
			int idx = thisSessionsRestaurants.size();
			try {
				idx = getARandomIndex(idx);
			} catch (IndexOutOfBoundsException ioobe) {
				System.out.println("Could not find any restaurants");
				return this.nothingFound;
			} catch (IllegalArgumentException iae) {
				System.out.println("Could not find any restaurants");
				return this.nothingFound;
			}
			Restaurant singleRestaurant = thisSessionsRestaurants.get(idx);
			String oneRestaurantKey = singleRestaurant.getRestaurantApiKey();
			
			try {
				//Call API to retrieve menu which returns JSON array of menu sections for that restaurant
				JSONArray menuSections = apiCaller.callApiToRetrieveMenu(oneRestaurantKey);
				
				//Call generate menu item list method to fill our list of menu items
				menuItemList = listFiller.fillMyMenuItemList(menuSections, singleRestaurant, declinedMenuItemRepo, key);
				
				//Using random generator to return random index value based on size of menu item list
				//Select random menu item and store it in current item, set valid index indicator to true
				if (menuItemList.size() > 1) {
					index = getARandomIndex(menuItemList.size() - 1);
					this.currentItem = menuItemList.get(index);
					weHaveAValidIndex = true;
				//If menu item list size is below threshold, remove restaurant from repository and while 
				//loop will run again to get new restaurant
				} else {
					restaurantRepo.delete(singleRestaurant);
					System.out.println("Exception: size of menu item list");
				} 
			} catch (IOException ioe) {
					System.out.println("Exception: input/output from API calls");
			}				
				
			//Use API caller to get an image JSON resource for single menu item
			menuImage = apiCaller.callApiToRetrieveMenuItemPictureURL(this.currentItem);
			
			//Pull single image link from JSON response and set it on current item
			//Switch valid photo indicator to true only when image is found
			if (menuImage == null) {
				this.currentItem.setImageURL("http://www.newdesignfile.com/postpic/2015/02/nophoto-available-clip-art-free_68016.png");
				weHaveAValidPhoto = true;
			} else {
				try {
					JSONArray imageArray = (JSONArray) menuImage.get("items");
					this.currentItem.setImageURL(imageArray.getJSONObject(0).get("link").toString());
					if (currentItem.getImageURL() != null) weHaveAValidPhoto = true;
				} catch (JSONException je) {
					System.out.println("Exception: could not find photo");
					declinedMenuItemRepo.save(this.currentItem);
					weHaveAValidPhoto = false;
				}
			}			
		}//End of while loop; should have single menu item with photo at this point
		return this.currentItem;
	}
	
	// Takes a size parameter and returns a random integer within that range
	private int getARandomIndex(int size) {
		return randomGenerator.nextInt(size);
	}
}
