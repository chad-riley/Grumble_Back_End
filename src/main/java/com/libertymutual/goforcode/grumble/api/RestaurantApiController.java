package com.libertymutual.goforcode.grumble.api;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.libertymutual.goforcode.grumble.models.MenuItem;
import com.libertymutual.goforcode.grumble.services.ApiCaller;
import com.libertymutual.goforcode.grumble.services.ListFiller;
import com.libertymutual.goforcode.grumble.services.MenuItemFinder;
import com.libertymutual.goforcode.grumble.services.MenuItemRepository;
import com.libertymutual.goforcode.grumble.services.RestaurantRepository;
import com.libertymutual.goforcode.grumble.services.SessionKeyGetter;

import us.monoid.json.JSONArray;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api")
public class RestaurantApiController {

	private RestaurantRepository restaurantRepo;
	private MenuItemRepository declinedMenuItemRepo;
	private MenuItem currentItem;
	private ApiCaller apiCaller;
	private ListFiller listFiller;
	private MenuItem nothingFound;
	private MenuItemFinder itemFinder;
	private SessionKeyGetter keyGetter;

	public RestaurantApiController(RestaurantRepository restaurantRepo, MenuItemRepository declinedMenuItemRepo) {
		this.restaurantRepo = restaurantRepo;
		this.declinedMenuItemRepo = declinedMenuItemRepo;
		this.apiCaller = new ApiCaller();
		this.listFiller = new ListFiller();
		this.itemFinder = new MenuItemFinder();
		this.keyGetter = new SessionKeyGetter();
		
		this.nothingFound = new MenuItem();
		this.nothingFound.setName("No results found in that location");
		this.nothingFound.setDescription("Please try a new location");
		this.nothingFound.setImageURL("https://media.giphy.com/media/forX81kqyzD4A/giphy.gif");
	}  

	@PostMapping("/{city}/{pickup_radius}/")
	public MenuItem newMenuItemRequest(@PathVariable String city, @PathVariable String pickup_radius, HttpServletRequest request) throws IOException, Exception {
		String key = keyGetter.getTheSessionKeyForRequest(request);
		
		restaurantRepo.deleteBySessionKey(key);

		JSONArray restaurantArray = new JSONArray();
		try {
			restaurantArray = apiCaller.callApiToRetrieveRestaurants(city, pickup_radius);
		} catch (IOException ioe) {
			return this.nothingFound;
		}		
		listFiller.fillMyListOfRestaurants(restaurantArray, restaurantRepo, key);
		
		this.currentItem = itemFinder.getASingleMenuItem(declinedMenuItemRepo, restaurantRepo, key);
		
		return this.currentItem;
	}
	
	@PostMapping("/{latitude}/{longitude}/{pickup_radius}/")
	public MenuItem newMenuItemRequestWithLatitudeAndLongitude(@PathVariable String latitude, @PathVariable String longitude, @PathVariable String pickup_radius, HttpServletRequest request) throws IOException, Exception {
		String key = keyGetter.getTheSessionKeyForRequest(request);
		
		restaurantRepo.deleteAll();

		JSONArray restaurantArray = new JSONArray();
		try {
			restaurantArray = apiCaller.callApiToRetrieveRestaurants(latitude, longitude, pickup_radius);
		} catch (IOException ioe) {
			return this.nothingFound;
		}
		listFiller.fillMyListOfRestaurants(restaurantArray, restaurantRepo, key);
		
		this.currentItem = itemFinder.getASingleMenuItem(declinedMenuItemRepo, restaurantRepo, key);
		
		return this.currentItem;
	}

	@PostMapping("/item")
	public MenuItem getAnotherMenuItem(HttpServletRequest request) throws Exception {
		String key = keyGetter.getTheSessionKeyForRequest(request);
		
		this.declinedMenuItemRepo.save(this.currentItem);
		
		this.currentItem = itemFinder.getASingleMenuItem(declinedMenuItemRepo, restaurantRepo, key);
		
		return this.currentItem;  
	}
}
