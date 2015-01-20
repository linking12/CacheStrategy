package cache;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 双链表实现LRU算法
 * 
 * 1. 新数据插入到链表头部； 
 * 2. 每当缓存命中（即缓存数据被访问），则将数据移到链表头部； 
 * 3. 当链表满的时候，将链表尾部的数据丢弃。
 * @author liushiming395
 * 
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> {
	private int MAX_CACHE_SIZE = 0;
	private Entry first;
	private Entry last;
	private Map<K, Entry<K, V>> hashMap;
	private Object lock = new Object();

	public LRUCache(int cacheSize) {
		MAX_CACHE_SIZE = cacheSize;
		hashMap = new HashMap<K, Entry<K, V>>();
	}

	public void put(K key, V value) {
		synchronized (lock) {
			Entry entry = getEntry(key);
			if (entry == null) {
				if (hashMap.size() >= MAX_CACHE_SIZE) {
					hashMap.remove(last.key);
					removeLast();
				}
				entry = new Entry();
				entry.key = key;
			}
			entry.value = value;
			moveToFirst(entry);
			hashMap.put(key, entry);
		}
	}

	public V get(K key) {
		synchronized (lock) {
			Entry<K, V> entry = getEntry(key);
			if (entry == null)
				return null;
			moveToFirst(entry);
			return entry.value;
		}
	}

	public void remove(K key) {
		synchronized (lock) {
			Entry entry = getEntry(key);
			if (entry != null) {
				if (entry.pre != null)
					entry.pre.next = entry.next;
				if (entry.next != null)
					entry.next.pre = entry.pre;
				if (entry == first)
					first = entry.next;
				if (entry == last)
					last = entry.pre;
			}
			hashMap.remove(key);
		}
	}

	private void moveToFirst(Entry entry) {
		if (entry == first)
			return;
		if (entry.pre != null)
			entry.pre.next = entry.next;
		if (entry.next != null)
			entry.next.pre = entry.pre;
		if (entry == last)
			last = last.pre;
		if (first == null || last == null) {
			first = last = entry;
			return;
		}
		entry.next = first;
		first.pre = entry;
		first = entry;
		entry.pre = null;
	}

	private void removeLast() {
		if (last != null) {
			last = last.pre;
			if (last == null)
				first = null;
			else
				last.next = null;
		}
	}

	private Entry<K, V> getEntry(K key) {
		return hashMap.get(key);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Entry entry = first;
		while (entry != null) {
			sb.append(String.format("%s:%s ", entry.key, entry.value));
			entry = entry.next;
		}
		return sb.toString();
	}

	class Entry<K, V> {
		public Entry pre;
		public Entry next;
		public K key;
		public V value;

	}
}
