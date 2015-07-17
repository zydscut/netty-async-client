package com.thomas.serialize;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomUtils;

public class City {
	private int pcode;
	private int ccode;
	private String name;
	
	private Map<String, District> districts = new HashMap<String, District>();
	
	public int getPcode() {
		return pcode;
	}
	public void setPcode(int pcode) {
		this.pcode = pcode;
	}
	public int getCcode() {
		return ccode;
	}
	public void setCcode(int ccode) {
		this.ccode = ccode;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getKey() {
		return pcode + "_" + ccode;
	}
	
	public void addDistrict(District district) {
		districts.put(district.getKey(), district);
	}
	
	public Map<String, District> getDistricts() {
		return districts;
	}
	
	public District randomDistrict() {
		int random = RandomUtils.nextInt(0, districts.size());
		District district = districts.get(pcode + "_" + ccode + "_" + random); 
		return district;
	}
}
