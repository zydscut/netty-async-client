package com.thomas.serialize;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.msgpack.MessagePack;
import org.msgpack.template.IntegerTemplate;
import org.msgpack.template.MapTemplate;
import org.msgpack.template.StringTemplate;
import org.msgpack.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;

public class SerializeTest {
	final static Logger logger = LoggerFactory.getLogger(SerializeTest.class);
	Map<String, String> address = new HashMap<String, String>();
	Map<String, Integer> score = new HashMap<String, Integer>();
	MessagePack msgp = new MessagePack();
	Template<Map<String, String>> strtpl = new MapTemplate<String, String>(
			StringTemplate.getInstance(), StringTemplate.getInstance());
	Template<Map<String, Integer>> inttpl = new MapTemplate<String, Integer>(
			StringTemplate.getInstance(), IntegerTemplate.getInstance());
	ObjectMapper mapper = new ObjectMapper();
	TypeReference<Map<String, String>> strref = new TypeReference<Map<String, String>>() {};
	TypeReference<Map<String, Integer>> intref = new TypeReference<Map<String, Integer>>() {};
	ObjectReader strreader = mapper.reader(strref);
	ObjectReader intreader = mapper.reader(intref);
	ObjectWriter writer = mapper.writer();
	Gson gson = new Gson();
	
	final static int tunetimes = 100000;
	
	@Before
	public void setUp() {
		Dsy dsy = Dsy.getInstance();
		Map<Integer, Province> provinces = dsy.get();
		int psize = provinces.size();
		for(int i = 0; i < 100; i ++) {
			String name = Name.getRandomName();
			int pnum = RandomUtils.nextInt(0, psize);
			Province province = provinces.get(pnum);
			City city = province.randomCity();
			
			if(city != null) {
				District district = city.randomDistrict();
				if(district != null) {
					address.put(name, province.getPname() + city.getName() + district.getName());				
				}
				else {
					address.put(name, province.getPname() + city.getName());
				}
			}
			else {
				address.put(name, province.getPname());
			}
			
			score.put(name, RandomUtils.nextInt(0, 101));
		}
	}
	
	public void testMsgpack() throws Exception {
		byte[] addb = msgp.write(address);
		byte[] scoreb = msgp.write(score);
		//Value addv = msgp.read(addb);
		
		Map<String, String> addm = msgp.read(addb, strtpl);
		Assert.assertEquals(address, addm);
		
		Map<String, Integer> scorem = msgp.read(scoreb, inttpl);
		Assert.assertEquals(score, scorem);
		
		logger.debug("msgpack : address byte size : " + addb.length);
		logger.debug("msgpack : score byte size : " + scoreb.length);
	}
	
	public void tuneMsgpack() throws Exception {
		try {
			String time = DateFormatUtils.format(new Date(), "hh:mm:ss:SSS");
			logger.debug("tune msgpack begin : " + time);
			
			for(int count = 0; count < tunetimes; count ++) {
				byte[] addb = msgp.write(address);
				byte[] scoreb = msgp.write(score);
				//Value addv = msgp.read(addb);
				
				Map<String, String> addm = msgp.read(addb, strtpl);
				Map<String, Integer> scorem = msgp.read(scoreb, inttpl);
			}
		}
		finally {
			String time = DateFormatUtils.format(new Date(), "hh:mm:ss:SSS");
			logger.debug("tune msgpack end : " + time);
		}
	}
	
	public void testJackson() throws Exception {
		byte[] addb = writer.writeValueAsBytes(address);
		byte[] scoreb = writer.writeValueAsBytes(score);
		
		Map<String, String> addm = strreader.readValue(addb);
		Map<String, Integer> scorem = intreader.readValue(scoreb);
		
		Assert.assertEquals(address, addm);
		Assert.assertEquals(score, scorem);
		
		logger.debug("jackson : address byte size : " + addb.length);
		logger.debug("jackson : score byte size : " + scoreb.length);
	}
	
	public void tuneJackson() throws Exception {
		try {
			String time = DateFormatUtils.format(new Date(), "hh:mm:ss:SSS");
			logger.debug("tune jackson begin : " + time);
			for(int count = 0; count < tunetimes; count ++) {
				byte[] addb = writer.writeValueAsBytes(address);
				byte[] scoreb = writer.writeValueAsBytes(score);
				
				Map<String, String> addm = strreader.readValue(addb);
				Map<String, Integer> scorem = intreader.readValue(scoreb);
			}
		}
		finally {
			String time = DateFormatUtils.format(new Date(), "hh:mm:ss:SSS");
			logger.debug("tune jackson end : " + time);
		}
	}
	
	public void testGson() {
		final int capacity = 8192; 
		//ByteArrayOutputStream baos = new ByteArrayOutputStream(capacity);
		//OutputStreamWriter oswrite = new OutputStreamWriter(baos);
		
		StringBuilder addsb = new StringBuilder(capacity);
		gson.toJson(address, addsb);
		//byte[] addb = addsb.toString().getBytes();
		//baos.toByteArray();
		String addstr = addsb.toString();
		
		Map<String, Object> addm = gson.fromJson(addstr, strref.getType());
		
		StringBuilder scoresb = new StringBuilder(capacity);
		gson.toJson(score, scoresb);
		String scorestr = scoresb.toString();
		
		Map<String, Object> scorem = gson.fromJson(scorestr, intref.getType());
		
		Assert.assertEquals(address, addm);
		Assert.assertEquals(score, scorem);
		
		//java string encoding with utf16
		logger.debug("gson : address byte size : " + addsb.length() * 2);
		logger.debug("gson : score byte size : " + scoresb.length() * 2);
	}
	
	public void tuneGson() throws Exception {
		try {
			String time = DateFormatUtils.format(new Date(), "hh:mm:ss:SSS");
			logger.debug("tune gson begin : " + time);
			for(int count = 0; count < tunetimes; count ++) {
				final int capacity = 8192; 
				//ByteArrayOutputStream baos = new ByteArrayOutputStream(capacity);
				//OutputStreamWriter oswrite = new OutputStreamWriter(baos);
				
				StringBuilder addsb = new StringBuilder(capacity);
				gson.toJson(address, addsb);
				//byte[] addb = addsb.toString().getBytes();
				//baos.toByteArray();
				String addstr = addsb.toString();
				
				Map<String, Object> addm = gson.fromJson(addstr, strref.getType());
				
				StringBuilder scoresb = new StringBuilder(capacity);
				gson.toJson(score, scoresb);
				String scorestr = scoresb.toString();
				
				Map<String, Object> scorem = gson.fromJson(scorestr, intref.getType());
			}
		}
		finally {
			String time = DateFormatUtils.format(new Date(), "hh:mm:ss:SSS");
			logger.debug("tune gson end : " + time);
		}
	}
	
	@Test
	public void testAll() throws Exception {
		testMsgpack();
		testJackson();
		testGson();
	}
	
	//@Test
	public void tuneAll() throws Exception {
		tuneMsgpack();
		tuneJackson();
		tuneGson();
	}
	
	@After
	public void tearDown() {
		
	}
}
