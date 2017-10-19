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
import us.monoid.json.JSONObject;

import org.junit.Test;

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
		
	} 

//	@Test
//	public void testFillMyListOfRestaurantsFillsAList() throws JSONException {
//		//arrange
//		String key = "123";
//		restaurantArray.put(jo);
//		
//		//act  
//		List<Restaurant> result = listFiller.fillMyListOfRestaurants(restaurantArray, restaurantRepo, key);
//		
//		//assert
//		assertThat(result.size()).isEqualTo(1);
//		assertThat(result.get(0).getRestaurantName()).isEqualTo("Test Restaurant");		
//	}

}
