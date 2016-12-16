package com.revecookie.hazelcast.nodes;


import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.instance.GroupProperties;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ClusterNode
{

	public static void main(String[] args) throws FileNotFoundException
	{
		Config config  = new XmlConfigBuilder("hazelcast.xml").build();

		config.setProperty(GroupProperties.PROP_WAIT_SECONDS_BEFORE_JOIN, "1");
		HazelcastInstance node = Hazelcast.newHazelcastInstance(config);

		User user1 = new User("Reveka", "1234");
		User user2 = new User("Mike", "4567");

		/* Example 1 -->  with key: String value: User */

		IMap<String, User> firstNodeUserMap = node.getMap("example1");
		firstNodeUserMap.put("first", user1);

		IMap<String, User> secondNodeUserMap = node.getMap("example1");
		User result1 = secondNodeUserMap.get("first");
		System.out.println(result1 == null ? "Example 1 failed" : result1.name);


		/* Example 2 -->  with key: String value: List<User> */

		List<User> userList = new ArrayList<User>();
		userList.add(user1);
		userList.add(user2);


		Map<String, List<User>> mapWithList = node.getMap("example2");
		mapWithList.put("first", userList);
		List<User> result2 = mapWithList.get("first");

		System.out.println(result2 == null || result2.isEmpty() ? "Example 2 failed" : result2.size());

		/* Example 3 --> with key: String value: List<User> but we try to manipulate the list reference */

		Map<String, List<User>> mapWithManipulatedList = node.getMap("example3");
		mapWithManipulatedList.put("first", new ArrayList<User>());
		List<User> list = (List<User>) node.getMap("example3").get("first");
		list.add(user1);
		list.add(user2);

		List<User> result3 = mapWithManipulatedList.get("first");

		System.out.println(result3 == null || result3.isEmpty() ? "Example 3 failed" : result3.size());

	}

	public static class User implements Serializable{
		public String name;
		public String pass;

		public User(String name, String pass)
		{
			this.name = name;
			this.pass = pass;
		}
	}

}
