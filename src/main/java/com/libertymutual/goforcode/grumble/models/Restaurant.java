package com.libertymutual.goforcode.grumble.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import us.monoid.json.JSONArray;
import us.monoid.json.JSONObject;


@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")

@Entity
public class Restaurant {

	@Id

	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(length = 255, nullable = false)
	private String restaurantApiKey;

	@Column(length = 255, nullable = false)
	private String restaurantName;

	@OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
	public List<MenuItem> menuItems;
	
//	private List<FoodTypes> foodTypes;

	private String address;

	private String city;

	private String state;

	private String zip;

	private String phone;

	private String latitude;

	private String longitude;
	
	private String foodType;
	
	private String url;

	public Restaurant() {}
	
	
	// getters and setters:

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRestaurantApiKey() {
		return restaurantApiKey;
	}

	public void setRestaurantApiKey(String restaurantApiKey) {
		this.restaurantApiKey = restaurantApiKey;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public void setRestaurantName(String restaurantName) {
		this.restaurantName = restaurantName;
	}

	public List<MenuItem> getMenuItems() {
		return menuItems;
	}

	public void setMenuItems(List<MenuItem> menuItems) {
		this.menuItems = menuItems;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getFoodType() {
		return foodType;
	}


	public void setFoodType(String foodType) {
		this.foodType = foodType;
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}

}

