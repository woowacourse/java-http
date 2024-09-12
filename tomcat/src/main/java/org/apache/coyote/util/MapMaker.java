package org.apache.coyote.util;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class MapMaker {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    public static <K> Collector<String[], ?, Map<K, String>> toMap(Function<String, K> keyMapper) {
        return Collectors.toMap(
                keyValue -> keyMapper.apply(keyValue[KEY_INDEX].trim()),
                keyValue -> keyValue[VALUE_INDEX].trim()
        );
    }
}
