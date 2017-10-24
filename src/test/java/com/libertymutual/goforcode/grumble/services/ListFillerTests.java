package com.libertymutual.goforcode.grumble.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.libertymutual.goforcode.grumble.models.MenuItem;
import com.libertymutual.goforcode.grumble.models.Restaurant;
import com.libertymutual.goforcode.grumble.services.ListFiller;
import com.libertymutual.goforcode.grumble.services.RestaurantRepository;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

public class ListFillerTests {
	
	ListFiller listFiller;
	JSONArray restaurantArray;
	RestaurantRepository restaurantRepo;
	JSONObject jo;

	@Before
	public void setUp() throws JSONException {
		listFiller = new ListFiller();
		restaurantArray = new JSONArray();  
		restaurantRepo = mock(RestaurantRepository.class);		
	} 

	@Test
	public void testFillMyListOfRestaurantsFillsAList() throws JSONException {
		//arrange
		String key = "123";
		jo = new JSONObject();
		jo.put("apiKey", "123");
		jo.put("name", "Test Restaurant");
		jo.put("latitude", "5");
		jo.put("longitude", "5");
		jo.put("streetAddress", "123 Main St");
		jo.put("city", "Seattle");
		jo.put("state", "WA");
		jo.put("zip", "55555");
		jo.put("phone", "555-555-5555");
		jo.put("foodTypes", "Mexican");
		jo.put("url", "www.somewhere.com");
		jo.put("sessionKey", "123");
		restaurantArray.put(jo);
		
		//act  
		listFiller.fillMyListOfRestaurants(restaurantArray, restaurantRepo, key);
		
		//assert
		verify(restaurantRepo).save(any(Restaurant.class));	
	}
	
	@Test 
	public void test_fillMyMenuItemList_returns_list_of_menu_items() throws JSONException, IOException {
		//arrange
		JSONArray menuSections = new JSONArray();
		JSONObject anItem = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject menuItem = new JSONObject();
		menuItem.put("name", "Test Dish One");
		menuItem.put("basePrice", "7.99");
		menuItem.put("description", "A single test dish");
		items.put(menuItem);
		anItem.put("items", items);
		menuSections.put(anItem);
		String key = "123";
		Restaurant restaurant = new Restaurant();
		MenuItem testItem = new MenuItem();
		testItem.setName("Test Dish One");
		testItem.setBasePrice("7.99");
		testItem.setDescription("A single test dish");
		
		//act
		List<MenuItem> result = listFiller.fillMyMenuItemList(menuSections, restaurant, key);
		
		//assert
		assertThat(result.get(0).getName()).isEqualTo(testItem.getName());
		assertThat(result.get(0).getBasePrice()).isEqualTo(testItem.getBasePrice());
		assertThat(result.get(0).getDescription()).isEqualTo(testItem.getDescription());
		assertThat(result.size()).isEqualTo(1);
	}
	
	@Test 
	public void test_fillMyMenuItemList_returns_no_menu_items() throws JSONException, IOException {
		//arrange
		JSONArray menuSections = new JSONArray();
		JSONObject anItem = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject menuItem = new JSONObject();
		menuItem.put("name", "Test Beverage Dish One");
		menuItem.put("basePrice", "7.99");
		menuItem.put("description", "A single test beverage dish");
		items.put(menuItem);
		anItem.put("items", items);
		menuSections.put(anItem);
		String key = "123";
		Restaurant restaurant = new Restaurant();
		
		//act
		List<MenuItem> result = listFiller.fillMyMenuItemList(menuSections, restaurant, key);
		
		//assert
		assertThat(result.size()).isEqualTo(0);
	}
	
	@Test 
	public void test_fillMyMenuItemList_with_no_description_returns_no_menu_items() throws JSONException, IOException {
		//arrange
		JSONArray menuSections = new JSONArray();
		JSONObject anItem = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject menuItem = new JSONObject();
		menuItem.put("name", "Test Dish One");
		menuItem.put("basePrice", "7.99");
		items.put(menuItem);
		anItem.put("items", items);
		menuSections.put(anItem);
		String key = "123";
		Restaurant restaurant = new Restaurant();
		
		//act
		List<MenuItem> result = listFiller.fillMyMenuItemList(menuSections, restaurant, key);
		
		//assert
		assertThat(result.size()).isEqualTo(0);
	}
	
	@Test 
	public void test_fillMyMenuItemList_with_low_price_returns_no_menu_items() throws JSONException, IOException {
		//arrange
		JSONArray menuSections = new JSONArray();
		JSONObject anItem = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject menuItem = new JSONObject();
		menuItem.put("name", "Test Dish One");
		menuItem.put("basePrice", "2.99");
		menuItem.put("description", "A single test dish");
		items.put(menuItem);
		anItem.put("items", items);
		menuSections.put(anItem);
		String key = "123";
		Restaurant restaurant = new Restaurant();
		
		//act
		List<MenuItem> result = listFiller.fillMyMenuItemList(menuSections, restaurant, key);
		
		//assert
		assertThat(result.size()).isEqualTo(0);
	}
	
	@Test 
	public void test_fillMyMenuItemList_with_high_price_returns_no_menu_items() throws JSONException, IOException {
		//arrange
		JSONArray menuSections = new JSONArray();
		JSONObject anItem = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject menuItem = new JSONObject();
		menuItem.put("name", "Test Dish One");
		menuItem.put("basePrice", "52.99");
		menuItem.put("description", "A single test dish");
		items.put(menuItem);
		anItem.put("items", items);
		menuSections.put(anItem);
		String key = "123";
		Restaurant restaurant = new Restaurant();
		
		//act
		List<MenuItem> result = listFiller.fillMyMenuItemList(menuSections, restaurant, key);
		
		//assert
		assertThat(result.size()).isEqualTo(0);
	}
	
	@Test 
	public void test_fillMyMenuItemList_with_party_returns_no_menu_items() throws JSONException, IOException {
		//arrange
		JSONArray menuSections = new JSONArray();
		JSONObject anItem = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject menuItem = new JSONObject();
		menuItem.put("name", "Test Party Dish One");
		menuItem.put("basePrice", "7.99");
		menuItem.put("description", "A single test dish");
		items.put(menuItem);
		anItem.put("items", items);
		menuSections.put(anItem);
		String key = "123";
		Restaurant restaurant = new Restaurant();
		
		//act
		List<MenuItem> result = listFiller.fillMyMenuItemList(menuSections, restaurant, key);
		
		//assert
		assertThat(result.size()).isEqualTo(0);
	}
	
	@Test 
	public void test_fillMyMenuItemList_with_catering_returns_no_menu_items() throws JSONException, IOException {
		//arrange
		JSONArray menuSections = new JSONArray();
		JSONObject anItem = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject menuItem = new JSONObject();
		menuItem.put("name", "Test Catering Dish One");
		menuItem.put("basePrice", "7.99");
		menuItem.put("description", "A single test dish");
		items.put(menuItem);
		anItem.put("items", items);
		menuSections.put(anItem);
		String key = "123";
		Restaurant restaurant = new Restaurant();
		
		//act
		List<MenuItem> result = listFiller.fillMyMenuItemList(menuSections, restaurant, key);
		
		//assert
		assertThat(result.size()).isEqualTo(0);
	}

	@Test 
	public void test_fillMyMenuItemList_with_soda_returns_no_menu_items() throws JSONException, IOException {
		//arrange
		JSONArray menuSections = new JSONArray();
		JSONObject anItem = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject menuItem = new JSONObject();
		menuItem.put("name", "Test Soda Dish One");
		menuItem.put("basePrice", "7.99");
		menuItem.put("description", "A single test dish");
		items.put(menuItem);
		anItem.put("items", items);
		menuSections.put(anItem);
		String key = "123";
		Restaurant restaurant = new Restaurant();
		
		//act
		List<MenuItem> result = listFiller.fillMyMenuItemList(menuSections, restaurant, key);
		
		//assert
		assertThat(result.size()).isEqualTo(0);
	}
	
	@Test 
	public void test_fillMyMenuItemList_with_drink_returns_no_menu_items() throws JSONException, IOException {
		//arrange
		JSONArray menuSections = new JSONArray();
		JSONObject anItem = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject menuItem = new JSONObject();
		menuItem.put("name", "Test Drink Dish One");
		menuItem.put("basePrice", "7.99");
		menuItem.put("description", "A single test dish");
		items.put(menuItem);
		anItem.put("items", items);
		menuSections.put(anItem);
		String key = "123";
		Restaurant restaurant = new Restaurant();
		
		//act
		List<MenuItem> result = listFiller.fillMyMenuItemList(menuSections, restaurant, key);
		
		//assert
		assertThat(result.size()).isEqualTo(0);
	}
	
	@Test 
	public void test_fillMyMenuItemList_with_sampler_returns_no_menu_items() throws JSONException, IOException {
		//arrange
		JSONArray menuSections = new JSONArray();
		JSONObject anItem = new JSONObject();
		JSONArray items = new JSONArray();
		JSONObject menuItem = new JSONObject();
		menuItem.put("name", "Test Sampler Dish One");
		menuItem.put("basePrice", "7.99");
		menuItem.put("description", "A single test dish");
		items.put(menuItem);
		anItem.put("items", items);
		menuSections.put(anItem);
		String key = "123";
		Restaurant restaurant = new Restaurant();
		
		//act
		List<MenuItem> result = listFiller.fillMyMenuItemList(menuSections, restaurant, key);
		
		//assert
		assertThat(result.size()).isEqualTo(0);
	}
}
