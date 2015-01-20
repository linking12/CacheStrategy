package cache;

public class CacheTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		lruCache1();
	}

	static void lruCache1() {
		System.out.println();
		System.out
				.println("===========================LRU ¡¥±Ì µœ÷===========================");
		LRUCache<Integer, String> lru = new LRUCache(5);
		lru.put(1, "11");
		lru.put(2, "11");
		lru.put(3, "11");
		lru.put(4, "11");
		lru.put(5, "11");
		System.out.println(lru.toString());
		lru.put(6, "66");
		lru.get(2);
		lru.put(7, "77");
		lru.get(4);
		System.out.println(lru.toString());
		System.out.println();
	}

}
