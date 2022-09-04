package org.apache.coyote.support;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class HttpHeaderFactory {

    public static HttpHeaders create(final Pair... pairs) {
        return new HttpHeaders(Arrays.stream(pairs)
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (x, y) -> y, LinkedHashMap::new)));
    }

    public static class Pair {

        private static final int PAIR_SIZE = 2;
        private static final int KEY_INDEX = 0;
        private static final int VALUE_INDEX = 1;

        private final String key;
        private final String value;

        public Pair(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        public static Pair splitBy(final String content, final String delimiter) {
            String[] header = content.split(delimiter);
            if (header.length != PAIR_SIZE) {
                throw new IllegalArgumentException();
            }
            return new Pair(header[KEY_INDEX].strip(), header[VALUE_INDEX].strip());
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
