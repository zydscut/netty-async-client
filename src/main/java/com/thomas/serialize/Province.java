package com.thomas.serialize;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;

public class Province {
	private int pcode;
	private String pname;
	
	private Map<String, City> cities = new HashMap<String, City>();
	
	public int getPcode() {
		return pcode;
	}
	public void setPcode(int pcode) {
		this.pcode = pcode;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	
	public void addCity(City city) {
		cities.put(city.getKey(), city);
	}
	
	public Map<String, City> getCities() {
		return cities;
	}
	
	public City randomCity() {
		int random = RandomUtils.nextInt(0, cities.size());
		City city = cities.get(pcode + "_" + random);
		return city;
	}
}
