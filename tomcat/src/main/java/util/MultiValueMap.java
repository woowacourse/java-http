package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MultiValueMap<K, V> {

    private final Map<K, List<V>> multiValueMap;

    public MultiValueMap() {
        this(0);
    }

    public MultiValueMap(int initialCapacity) {
        this.multiValueMap = new LinkedHashMap<>(initialCapacity);
    }

    public void put(final K key, final V value) {
        if (isNotContain(key)) {
            List<V> values = new ArrayList<>();
            values.add(value);
            multiValueMap.put(key, values);
            return;
        }

        multiValueMap.get(key).add(value);
    }

    public void putAll(final K key, final List<V> values) {
        if (isNotContain(key)) {
            multiValueMap.put(key, new ArrayList<>(values));
            return;
        }

        multiValueMap.get(key).addAll(values);
    }

    public V getRecentValue(final K key) {
        List<V> values = multiValueMap.get(key);
        if (values == null) {
            return null;
        }
        return values.get(values.size()-1);
    }

    public List<V> getValues(final K key) {
        return multiValueMap.get(key);
    }

    public boolean isContain(final K key){
        return this.multiValueMap.containsKey(key);
    }

    private boolean isNotContain(final K key) {
        return !multiValueMap.containsKey(key);
    }

    public Map<K, List<V>> getMultiValueMap() {
        return multiValueMap;
    }
}
