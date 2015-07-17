package com.thomas.serialize;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.MessagePack;
import org.msgpack.template.MapTemplate;
import org.msgpack.template.StringTemplate;
import org.msgpack.template.Template;

public class MsgPackTest {
	public static void main(String[] args) throws IOException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("t", "aa1");
		map.put("f", "111");

		MessagePack msgp = new MessagePack();

		byte[] raw = msgp.write(map);

		Template<Map<String, String>> tpl = new MapTemplate<String, String>(
				StringTemplate.getInstance(), StringTemplate.getInstance());
		msgp.read(raw, tpl);
		// msgp.read(raw);

		System.out.println(map.get("t").toString() + " : " + map.get("f").toString());
	}
}
