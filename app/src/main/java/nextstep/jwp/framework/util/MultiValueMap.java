package nextstep.jwp.framework.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiValueMap<K, V> {

    private final Map<K, List<V>> multiValueMap;

    public MultiValueMap() {
        this(new HashMap<>());
    }

    public MultiValueMap(Map<K, List<V>> multiValueMap) {
        this.multiValueMap = multiValueMap;
    }

    public void addAll(K key, List<V> values) {
        this.multiValueMap.merge(key, values, (oldValues, newValues) -> {
            oldValues.addAll(newValues);
            return oldValues;
        });
    }
}
