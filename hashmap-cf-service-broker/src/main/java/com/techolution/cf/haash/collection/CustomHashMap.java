package com.techolution.cf.haash.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This is Custom HashMap implementation with some generic methods , Not all
 * 
 * @author Prithvish Mukherjee
 *
 * @param <K>
 * @param <V>
 */
@JsonInclude(Include.NON_NULL)
public class CustomHashMap<K, V> {

	private int DEFAULT_BUCKET_COUNT = 5;

	private CustomEntry<K, V>[] buckets;

	@SuppressWarnings("unchecked")
	public CustomHashMap() {
		buckets = new CustomEntry[DEFAULT_BUCKET_COUNT];
	}

	@JsonIgnore
	public int getDEFAULT_BUCKET_COUNT() {
		return DEFAULT_BUCKET_COUNT;
	}

	public void setDEFAULT_BUCKET_COUNT(int dEFAULT_BUCKET_COUNT) {
		DEFAULT_BUCKET_COUNT = dEFAULT_BUCKET_COUNT;
	}

	@JsonIgnore
	public CustomEntry<K, V>[] getBuckets() {
		return buckets;
	}

	public void setBuckets(CustomEntry<K, V>[] buckets) {
		this.buckets = buckets;
	}

	@SuppressWarnings("unchecked")
	public CustomHashMap(int capacity) {
		buckets = new CustomEntry[capacity];
	}

	private void throwIfKeyNull(K key) {
		if (key == null) {
			throw new IllegalArgumentException("KEY cannot be null");
		}
	}

	public void clear() {
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = null;
		}
	}

	public boolean containsKey(K key) {
		throwIfKeyNull(key);
		for (CustomEntry<K, V> customEntry : buckets) {
			if (null == customEntry)
				continue;
			CustomEntry<K, V> current = customEntry;
			while (null != current) {
				if (key.equals(current.getKey())) {
					return true;
				}
				current = current.getNext();
			}
		}
		return false;
	}

	public Set<CustomEntry<K, V>> entrySet() {
		Set<CustomEntry<K, V>> entrySet = new HashSet<CustomEntry<K, V>>();
		for (CustomEntry<K, V> entry : buckets) {
			CustomEntry<K, V> current = entry;
			if (current == null)
				continue;
			while (null != current) {
				// System.out.println("adding : " + current.getValue());
				entrySet.add(current);
				current = current.getNext();
			}
		}
		return entrySet;
	}

	public V get(K key) {
		throwIfKeyNull(key);
		CustomEntry<K, V> entry = buckets[getBucketForKey(key)];
		while (entry != null && !key.equals(entry.getKey()))
			entry = entry.getNext();
		return null != entry ? entry.getValue() : null;
	}

	private int getBucketForKey(K key) {
		return Math.abs((key.hashCode() * 7) % buckets.length); // 0 .. length
																// -1
	}

	public boolean isEmpty() {
		for (CustomEntry<K, V> customEntry : buckets) {
			if (null != customEntry)
				return false;
		}
		return true;
	}

	public Set<K> keySet() {
		Set<K> keyset = new HashSet<K>();
		for (CustomEntry<K, V> customEntry : buckets) {
			CustomEntry<K, V> current = customEntry;
			while (null != current) {
				keyset.add(current.getKey());
				current = current.getNext();
			}
		}
		return keyset;
	}

	public V put(K key, V value) {
		throwIfKeyNull(key);
		int bucketIndex = getBucketForKey(key);
		V oldVal = null;
		CustomEntry<K, V> newEntry = new CustomEntry<K, V>(key, value);
		if (buckets[bucketIndex] == null) {
			buckets[bucketIndex] = newEntry;
		} else {
			CustomEntry<K, V> prev = null;
			CustomEntry<K, V> current = buckets[bucketIndex];
			while (current != null) {
				// if key matches then overwrite the entries
				if (key.equals(current.getKey())) {
					if (null == prev) {// first value in LinkedList
						newEntry.setNext(current.getNext());
						buckets[bucketIndex] = newEntry;
						return current.getValue();
					} else {
						newEntry.setNext(current.getNext());
						prev.setNext(newEntry);
						return current.getValue();
					}
				}

				prev = current;
				current = current.getNext();
			}
			prev.setNext(newEntry);
		}
		return oldVal;
	}

	public V remove(K key) {
		int bucketIndex = getBucketForKey(key);
		if (buckets[bucketIndex] == null)
			return null;
		else {
			CustomEntry<K, V> prev = null;
			CustomEntry<K, V> current = buckets[bucketIndex];

			while (current != null) {
				if (key.equals(current.getKey())) {
					if (prev == null) {
						V oldVal = buckets[bucketIndex].getValue();
						buckets[bucketIndex] = buckets[bucketIndex].getNext();
						return oldVal;
					} else {
						prev.setNext(current.getNext());
						return current.getValue();
					}
				}
				prev = current;
				current = current.getNext();
			}
		}
		return null;

	}

	public int size() {
		return entrySet().size();
	}

	public Collection<V> values() {
		ArrayList<V> values = new ArrayList<V>();
		for (CustomEntry<K, V> customEntry : buckets) {
			CustomEntry<K, V> current = customEntry;
			while (null != current) {
				values.add(current.getValue());
				current = current.getNext();
			}
		}
		return values;
	}

}
