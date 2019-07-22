package com.si.upstream.common.util;


import java.lang.reflect.Array;
import java.util.*;

public class SetUtil {

	public static <T> List<T> wrap(T obj){
		List<T> res = new ArrayList<>();
		res.add(obj);
		return res;
	}
	
	public static <T> List<T> trimWrap(T[] arr){
		List<T> res = new ArrayList<>();
		if(arr == null)
			return res;
		for(T v : arr){
			if(v != null)
				res.add(v);
		}
		return res;
	}
	
	public static <T> T first(T[] arr){
		if(arr == null || arr.length < 1)
			return null;
		return arr[0];
	}
	
	public static <T> T first(Iterable<T> collect){
		if(collect == null)
			return null;
		Iterator<T> iterator = collect.iterator();
		if(iterator.hasNext())
			return iterator.next();
		return null;
	}

	public static <T> T rand(Collection<T> collect){
		if(collect == null) return null;

		int size = collect.size();
		if(size < 1) return null;

		int inx = new Random().nextInt(size);

		if(collect instanceof List && collect instanceof RandomAccess) {
			return ((List<T>) collect).get(inx);
		} else {
			Iterator<T> iterator = collect.iterator();
			while(iterator.hasNext()) {
				if (inx-- > 0)
					iterator.next();
				else
					return iterator.next();
			}
			return null;
		}
	}

	public static <T> T rand(T[] arr){
		if(arr == null)
			return null;
		int size = arr.length;
		if(size < 1)
			return null;
		return arr[new Random().nextInt(size)];
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] some(T[] arr, int count){
		if(arr == null)
			return null;
		if(count < 0)
			count = 0;

		T[] res = (T[]) Array.newInstance(arr.getClass().getComponentType(), count);
		if(count < 1)
			return res;

		int size = arr.length;
		if(size < 1)
			return res;
		Random rnd = new Random();
		int[] inxArr = new int[size]; // 默认0 故用 +1 标记
		int getInx, getVal;

		for(int i=0; i<count; i++){
			getInx = i + rnd.nextInt(size - i);
			getVal = inxArr[getInx];
			inxArr[getInx] = i + 1;
			res[i] = arr[getVal == 0 ? getInx : getVal - 1];
		}
		return res;
	}
	
	private static final String EMPTY_STR = "";
	
	public static <K, V> Set<V> line(List<Map<K, V>> ml, K k){
		if(ml == null || ml.isEmpty())
			return new HashSet<>();
		Set<V> s = new HashSet<>();
		for(Map<K, V> m : ml){
			V v = m.get(k);
			if(v == null || EMPTY_STR.equals(v))
				continue;
			s.add(v);
		}
		return s;
	}
	
	public static <K, V> void pick(Map<K, List<V>> map, K k, V v){
		if(map == null || k == null)
			return ;
		List<V> list = map.get(k);
		if(list == null){
			list = new ArrayList<>();
			map.put(k, list);
		}
		if(v != null)
			list.add(v);
	}
	
	public static int[] shuffleIndex(int count){
		int[] shuffleInx = new int[count];
		for(int i=0; i<count; i++)
			shuffleInx[i] = i;
		
		Random rnd = new Random();
		for(int i=0; i<count; i++){
			swap(shuffleInx, i, rnd.nextInt(count));
		}
		return shuffleInx;
	}
	
	private static void swap(int[] shuffleInx, int i, int j){
		int t = shuffleInx[i];
		shuffleInx[i] = shuffleInx[j];
		shuffleInx[j] = t;
	}
}