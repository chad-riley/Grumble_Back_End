package com.libertymutual.goforcode.grumble.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class MenuItem {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(length=255, nullable=false)
	private String name;
	
	@Column(length=500)
	private String description;
	
	private String imageURL;
	
	private String basePrice;
	
	private String menuApiKey;
	
	public MenuItem() {}	
	
	public MenuItem(String name) {
		this.name = name;
	}
	
	@ManyToOne
	private Restaurant restaurant;

	private String sessionKey;
	
	private String menuSectionName;

	private boolean itemHasBeenRejected = false;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public String getBasePrice() {
		return basePrice;

	}

	public void setBasePrice(String basePrice) {
		this.basePrice = basePrice;
	}

	public String getMenuApiKey() {
		return menuApiKey;
	}

	public void setMenuApiKey(String menuApiKey) {
		this.menuApiKey = menuApiKey;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}
	
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	public String getSessionKey() {
		return this.sessionKey;
	}
	
	public void setItemHasBeenRejected(boolean itemHasBeenRejected) {
		this.itemHasBeenRejected = itemHasBeenRejected;
	}
	
	public boolean getItemHasBeenRejected() {
		return this.itemHasBeenRejected;
	}

	public void setMenuSectionName(String menuSectionName) {
		this.menuSectionName = menuSectionName;
	}
	
	public String getMenuSectionName() {
		return this.menuSectionName;
	}

}
