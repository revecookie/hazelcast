package com.revecookie.hazelcast.nodes;

import com.hazelcast.config.Config;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.instance.GroupProperties;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class HelperNode
{
	public static void main(String[] args) throws FileNotFoundException
	{
		Config config = new XmlConfigBuilder("hazelcast.xml").build();

		config.setProperty(GroupProperties.PROP_WAIT_SECONDS_BEFORE_JOIN, "1");
		HazelcastInstance node = Hazelcast.newHazelcastInstance(config);

		/* Example 1 -->  with key: String value: User */

		IMap<String, ClusterNode.User> userMap = node.getMap("example1");
		ClusterNode.User result1 = userMap.get("first");
		System.out.println(result1 == null ? "Example 1 failed" : result1.name);


		/* Example 2 -->  with key: String value: List<User> */

		Map<String, List<ClusterNode.User>> mapWithList = node.getMap("example2");
		List<ClusterNode.User> result2 = mapWithList.get("first");

		System.out.println(result2 == null || result2.isEmpty() ? "Example 2 failed" : result2.size());

		/* Example 3 -->  with key: String value: List<User> but we try to manipulate the list reference */

		Map<String, List<ClusterNode.User>> mapWithManipulatedList = node.getMap("example3");
		List<ClusterNode.User> result3 = mapWithManipulatedList.get("first");

		System.out.println(result3 == null || result3.isEmpty() ? "Example 3 failed" : result3.size());

	}
}
