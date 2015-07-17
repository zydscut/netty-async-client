package com.thomas.serialize;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * for district, useless at the moment
 * @author thomas.zheng
 *
 */
public class Dsy {
	Map<Integer, Province> provinces = new HashMap<Integer, Province>();
	Map<String, City> cities = new HashMap<String, City>();
	Map<String, District> districts = new HashMap<String, District>();
	
	private Dsy() {
		
		List<String> lines = new ArrayList<String>();
		try {
			InputStream in = new FileInputStream("src/main/java/com/thomas/serialize/dsy.txt");
			lines = IOUtils.readLines(in);
			init(lines);
		}
		catch (IOException ie) {
			ie.printStackTrace();
		}
	};
	
	private void init(List<String> lines) {
		for(String line : lines) {
			if(! StringUtils.isEmpty(line)) {
				String[] lineSpt = line.split(" ");
				if(lineSpt.length == 2) {
					String code = lineSpt[0];
					String descs = lineSpt[1];
					TypeToken<List<String>> token = new TypeToken<List<String>>(){};
					Gson gson = new Gson();
					List<String> descLst = gson.fromJson(descs, token.getType());
					
					if(code.matches("\\b\\d+\\b")) {
						//province 
						//0,["安徽省|1","北京市|2","福建省|3","甘肃省|4"...]
						for(String desc : descLst) {
							String[] descSpt = desc.split("\\|");
							if(descSpt.length == 2) {
								String pname = descSpt[0];
								Integer pcode = Integer.parseInt(descSpt[1]) - 1;
								Province province = new Province();
								province.setPcode(pcode);
								province.setPname(pname);
								provinces.put(pcode, province);
							}
						}
					}
					else if (code.matches("\\b\\d+_\\d+\\b")){
						//city
						//0_4 ["潮州市|34","东莞市|56","佛山市|61","广州市|73"...]
						String[] codeSplits = code.split("_");
						if(codeSplits.length == 2) {
							for(int index = 0; index < descLst.size(); index ++) {
								String desc = descLst.get(index);
								String[] descSpt = desc.split("\\|");
								if(descSpt.length == 2) {
									String name = descSpt[0];
									City city = new City();
									city.setCcode(index);
									city.setPcode(Integer.parseInt(codeSplits[1]));
									city.setName(name);
									cities.put(city.getKey(), city);
								}
							}
						}
					}
					else if(code.matches("\\b\\d+_\\d+_\\d+\\b")) {
						String[] codeSplits = code.split("_");
						
						if(codeSplits.length == 3) {
							for(int index = 0; index < descLst.size(); index ++) {
								String desc = descLst.get(index);
								String[] descSpt = desc.split("\\|");
								if(descSpt.length == 2) {
									String name = descSpt[0];
									District district = new District();
									district.setPcode(Integer.parseInt(codeSplits[1]));
									district.setCcode(Integer.parseInt(codeSplits[2]));
									district.setDcode(index);
									district.setName(name);
									districts.put(district.getKey(), district);
								}
							}
						}
					}
				}
			}
		}
		
		for(String dkey : districts.keySet()) {
			District district = districts.get(dkey);
			City city = get(district.getPcode(), district.getCcode());
			city.addDistrict(district);
		}
		
		for(String ckey : cities.keySet()) {
			City city = cities.get(ckey);
			Province province = get(city.getPcode());
			province.addCity(city);
		}
		
	}
	
	public Map<Integer, Province> get() {
		return provinces;
	}
	
	public Province get(int province) {
		return provinces.get(province);
	}
	
	public City get(int province, int city) {
		return cities.get(province + "_" + city);
	}
	
	private static Dsy instance;
	
	public static Dsy getInstance() {
		if(instance == null) {
			instance = new Dsy();
		}
		return instance;
	}
}