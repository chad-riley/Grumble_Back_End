package com.libertymutual.goforcode.grumble.models;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.dao.EmptyResultDataAccessException;

import com.google.gson.JsonArray;
import com.libertymutual.goforcode.grumble.services.ListFiller;
import com.libertymutual.goforcode.grumble.services.RestaurantRepository;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONException;

import org.junit.Test;

public class ListFillerTests {
	
	ListFiller listFiller;
	JSONArray restaurantArray;
	RestaurantRepository restaurantRepo;
	
	String payload = "{\r\n" + 
			"  \"address\": {\r\n" + 
			"    \"apiKey\": null,\r\n" + 
			"    \"streetAddress\": null,\r\n" + 
			"    \"latitude\": 47.4684908,\r\n" + 
			"    \"longitude\": -122.2463847,\r\n" + 
			"    \"city\": null,\r\n" + 
			"    \"state\": null,\r\n" + 
			"    \"zip\": null,\r\n" + 
			"    \"aptNumber\": null\r\n" + 
			"  },\r\n" + 
			"  \"restaurants\": [\r\n" + 
			"    {\r\n" + 
			"      \"apiKey\": \"90fd4587554469b1144247b91fbcb2f3277d9670b3dd028e\",\r\n" + 
			"      \"deliveryMin\": 20,\r\n" + 
			"      \"deliveryPrice\": 10,\r\n" + 
			"      \"logoUrl\": \"https://eatstreet-static.s3.amazonaws.com/assets/images/restaurant_logos/angel-city-deli-21688_1419613835539.png\",\r\n" + 
			"      \"name\": \"Angel City Deli\",\r\n" + 
			"      \"streetAddress\": \"12621 Renton Ave S\",\r\n" + 
			"      \"city\": \"Seattle\",\r\n" + 
			"      \"state\": \"WA\",\r\n" + 
			"      \"zip\": \"98178\",\r\n" + 
			"      \"foodTypes\": [\r\n" + 
			"        \"Subs & Sandwiches\",\r\n" + 
			"        \"Wings\",\r\n" + 
			"        \"BBQ\",\r\n" + 
			"        \"Southern Food\",\r\n" + 
			"        \"Healthy Food\",\r\n" + 
			"        \"Breakfast\"\r\n" + 
			"      ],\r\n" + 
			"      \"phone\": \"(206) 772-2223\",\r\n" + 
			"      \"latitude\": 47.49104,\r\n" + 
			"      \"longitude\": -122.23975,\r\n" + 
			"      \"minFreeDelivery\": 0,\r\n" + 
			"      \"taxRate\": 0.095,\r\n" + 
			"      \"acceptsCash\": true,\r\n" + 
			"      \"acceptsCard\": true,\r\n" + 
			"      \"offersPickup\": true,\r\n" + 
			"      \"offersDelivery\": true,\r\n" + 
			"      \"isTestRestaurant\": false,\r\n" + 
			"      \"minWaitTime\": 45,\r\n" + 
			"      \"maxWaitTime\": 60,\r\n" + 
			"      \"open\": true,\r\n" + 
			"      \"url\": \"https://eatstreet.com/seattle-wa/restaurants/angel-city-deli\",\r\n" + 
			"      \"hours\": {\r\n" + 
			"        \"Monday\": [\r\n" + 
			"          \"11:00 AM - 8:45 PM\"\r\n" + 
			"        ],\r\n" + 
			"        \"Saturday\": [\r\n" + 
			"          \"11:00 AM - 8:45 PM\"\r\n" + 
			"        ],\r\n" + 
			"        \"Wednesday\": [\r\n" + 
			"          \"11:00 AM - 8:45 PM\"\r\n" + 
			"        ],\r\n" + 
			"        \"Tuesday\": [\r\n" + 
			"          \"11:00 AM - 8:45 PM\"\r\n" + 
			"        ],\r\n" + 
			"        \"Friday\": [\r\n" + 
			"          \"11:00 AM - 8:45 PM\"\r\n" + 
			"        ],\r\n" + 
			"        \"Thursday\": [\r\n" + 
			"          \"11:00 AM - 8:45 PM\"\r\n" + 
			"        ]\r\n" + 
			"      },\r\n" + 
			"      \"timezone\": \"US/Pacific\"\r\n" + 
			"    }";
	
	public void setUp() throws JSONException {
		listFiller = new ListFiller();
		restaurantArray = new JSONArray(payload);  
		restaurantRepo = mock(RestaurantRepository.class);
	} 

	@Test
	public void testFillMyListOfRestaurantsFillsAList() throws JSONException {
		//arrange
		List<Restaurant> restaurantList = new ArrayList<Restaurant>(); 
		Restaurant restaurant = new Restaurant();
		
		restaurant.setRestaurantApiKey(restaurantArray.getJSONObject(0).getString("apiKey"));
		restaurant.setRestaurantName(restaurantArray.getJSONObject(0).getString("name"));
		restaurant.setLatitude(restaurantArray.getJSONObject(0).getString("latitude"));
		restaurant.setLongitude(restaurantArray.getJSONObject(0).getString("longitude"));
		restaurant.setAddress(restaurantArray.getJSONObject(0).getString("streetAddress"));
		restaurant.setCity(restaurantArray.getJSONObject(0).getString("city"));
		restaurant.setState(restaurantArray.getJSONObject(0).getString("state"));
		restaurant.setZip(restaurantArray.getJSONObject(0).getString("zip"));
		restaurant.setPhone(restaurantArray.getJSONObject(0).getString("phone"));
		restaurant.setFoodType(restaurantArray.getJSONObject(0).getString("foodTypes"));
		restaurant.setUrl(restaurantArray.getJSONObject(0).getString("url"));
		restaurantList.add(restaurant);
		restaurantRepo.save(restaurant);

		//when(restaurantRepo.save(restaurant)).thenReturn(restaurantList);
		
		//act  
		List<Restaurant> result = listFiller.fillMyListOfRestaurants(restaurantArray, restaurantRepo);
		
		//assert
		assertThat(result.size()).isEqualTo(1);
		assertThat(result.get(0)).isSameAs(restaurantList.get(0));
		
	}

}
