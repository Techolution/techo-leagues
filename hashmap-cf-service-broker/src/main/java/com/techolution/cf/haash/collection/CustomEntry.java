package com.techolution.cf.haash.collection;

/**
 * 
 * @author Prithvish Mukherjee
 *
 * @param <K>
 * @param <V>
 */
public class CustomEntry<K, V> {

	private final K key;
	private V value;
	private CustomEntry<K, V> next;

	public CustomEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((next == null) ? 0 : next.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomEntry other = (CustomEntry) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (next == null) {
			if (other.next != null)
				return false;
		} else if (!next.equals(other.next))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public CustomEntry(K key, V value, CustomEntry<K, V> next) {
		this.key = key;
		this.value = value;
		this.next = next;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	public CustomEntry<K, V> getNext() {
		return next;
	}

	public void setNext(CustomEntry<K, V> next) {
		this.next = next;
	}

	public K getKey() {
		return key;
	}

}