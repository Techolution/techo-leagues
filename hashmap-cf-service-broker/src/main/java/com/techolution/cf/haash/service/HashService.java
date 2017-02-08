package com.techolution.cf.haash.service;

import org.springframework.stereotype.Service;

import com.techolution.cf.haash.collection.CustomHashMap;

@Service
public class HashService {
	private CustomHashMap<String, CustomHashMap<Object, Object>> hashMaps = new CustomHashMap<String, CustomHashMap<Object, Object>>();

	public void create(String id) {
		hashMaps.put(id, new CustomHashMap<Object, Object>());
	}

	public void delete(String id) {
		hashMaps.remove(id);
	}

	public void put(String id, Object key, Object value) {
		CustomHashMap<Object, Object> mapInstance = hashMaps.get(id);
		mapInstance.put(key, value);
	}

	public Object get(String id, Object key) {
		CustomHashMap<Object, Object> mapInstance = hashMaps.get(id);
		if (null != mapInstance)
			return mapInstance.get(key);
		return null;
	}

	public void delete(String id, Object key) {
		CustomHashMap<Object, Object> mapInstance = hashMaps.get(id);
		mapInstance.remove(key);
	}
}
