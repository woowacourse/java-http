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
        validateKey(key);

        List<? extends V> values = multiValueMap.get(key);
        return values.get(values.size()-1);
    }

    public List<V> getValues(final K key) {
        validateKey(key);

        return multiValueMap.get(key);
    }

    private boolean isNotContain(final K key) {
        return !multiValueMap.containsKey(key);
    }

    private void validateKey(final K key) {
        if(isNotContain(key)) {
            throw new IllegalArgumentException("해당 키가 존재하지 않습니다.");
        }
    }

    public Map<K, List<V>> getMultiValueMap() {
        return multiValueMap;
    }
}
