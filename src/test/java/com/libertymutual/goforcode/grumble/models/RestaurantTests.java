package com.libertymutual.goforcode.grumble.models;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.meanbean.test.BeanTester;
import org.springframework.dao.EmptyResultDataAccessException;

import org.junit.Test;

public class RestaurantTests {
	
	Restaurant restaurant;
	
	@Before
	public void setUp() {
		restaurant = new Restaurant();
	}
	
	@Test
	public void test_all_getters_and_setters() {
		new BeanTester().testBean(Restaurant.class);
	}

}

