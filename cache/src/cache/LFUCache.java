package cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * 
 * LFU的每个数据块都有一个引用计数，所有数据块按照引用计数排序，具有相同引用计数的数据块则按照时间排序
 * 
 * 1. 新加入数据插入到队列尾部（因为引用计数为1）； 
 * 2. 队列中的数据被访问后，引用计数增加，队列重新排序；
 * 3. 当需要淘汰数据时，将已经排序的列表最后的数据块删除。
 * 
 * @author liushiming395
 * 
 * @param <K>
 * @param <V>
 */
public class LFUCache<K, V> {
	private final Map<K, CacheNode<K, V>> cache;
	private final LinkedHashSet[] frequencyList;
	private int lowestFrequency;
	private int maxFrequency;

	private final int maxCacheSize;
	private final float evictionFactor;

	public LFUCache(int maxCacheSize, float evictionFactor) {
		if (evictionFactor <= 0 || evictionFactor >= 1) {
			throw new IllegalArgumentException(
					"Eviction factor must be greater than 0 and lesser than or equal to 1");
		}
		this.cache = new HashMap<K, CacheNode<K, V>>(maxCacheSize);
		this.frequencyList = new LinkedHashSet[maxCacheSize];
		this.lowestFrequency = 0;
		this.maxFrequency = maxCacheSize - 1;
		this.maxCacheSize = maxCacheSize;
		this.evictionFactor = evictionFactor;
		initFrequencyList();
	}

	private void initFrequencyList() {
		for (int i = 0; i <= maxFrequency; i++) {
			frequencyList[i] = new LinkedHashSet<CacheNode<K, V>>();
		}
	}

	public void put(K k, V v) {
		V oldValue = null;
		CacheNode<K, V> currentNode = cache.get(k);
		if (currentNode == null) {
			if (cache.size() == maxCacheSize) {
				doEviction();
			}
			LinkedHashSet<CacheNode<K, V>> nodes = frequencyList[0];
			currentNode = new CacheNode(k, v, 0);
			nodes.add(currentNode);
			cache.put(k, currentNode);
			lowestFrequency = 0;
		} else {
			oldValue = currentNode.value;
			currentNode.value = v;
		}
	}

	public V get(Object k) {
		CacheNode<K, V> currentNode = cache.get(k);
		if (currentNode != null) {
			int currentFrequency = currentNode.frequency;
			if (currentFrequency < maxFrequency) {
				int nextFrequency = currentFrequency + 1;
				LinkedHashSet<CacheNode<K, V>> currentNodes = frequencyList[currentFrequency];
				LinkedHashSet<CacheNode<K, V>> newNodes = frequencyList[nextFrequency];
				moveToNextFrequency(currentNode, nextFrequency, currentNodes,
						newNodes);
				cache.put((K) k, currentNode);
				if (lowestFrequency == currentFrequency
						&& currentNodes.isEmpty()) {
					lowestFrequency = nextFrequency;
				}
			} else {
				// Hybrid with LRU: put most recently accessed ahead of others:
				LinkedHashSet<CacheNode<K, V>> nodes = frequencyList[currentFrequency];
				nodes.remove(currentNode);
				nodes.add(currentNode);
			}
			return currentNode.value;
		} else {
			return null;
		}
	}

	public void remove(Object k) {
		CacheNode<K, V> currentNode = cache.remove(k);
		if (currentNode != null) {
			LinkedHashSet<CacheNode<K, V>> nodes = frequencyList[currentNode.frequency];
			nodes.remove(currentNode);
			if (lowestFrequency == currentNode.frequency) {
				findNextLowestFrequency();
			}
		}
	}

	private void doEviction() {
		int currentlyDeleted = 0;
		float target = maxCacheSize * evictionFactor;
		while (currentlyDeleted < target) {
			LinkedHashSet<CacheNode<K, V>> nodes = frequencyList[lowestFrequency];
			if (nodes.isEmpty()) {
				throw new IllegalStateException(
						"Lowest frequency constraint violated!");
			} else {
				Iterator<CacheNode<K, V>> it = nodes.iterator();
				while (it.hasNext() && currentlyDeleted++ < target) {
					CacheNode<K, V> node = it.next();
					it.remove();
					cache.remove(node.key);
				}
				if (!it.hasNext()) {
					findNextLowestFrequency();
				}
			}
		}
	}

	private void moveToNextFrequency(CacheNode<K, V> currentNode,
			int nextFrequency, LinkedHashSet<CacheNode<K, V>> currentNodes,
			LinkedHashSet<CacheNode<K, V>> newNodes) {
		currentNodes.remove(currentNode);
		newNodes.add(currentNode);
		currentNode.frequency = nextFrequency;
	}

	private void findNextLowestFrequency() {
		while (lowestFrequency <= maxFrequency
				&& frequencyList[lowestFrequency].isEmpty()) {
			lowestFrequency++;
		}
		if (lowestFrequency > maxFrequency) {
			lowestFrequency = 0;
		}
	}

	class CacheNode<K, V> {
		public K key;
		public V value;
		public int frequency;

		public CacheNode(K k, V v, int frequency) {
			this.key = k;
			this.value = v;
			this.frequency = frequency;
		}
	}
}
