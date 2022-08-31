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

        private final String key;
        private final String value;

        public Pair(final String key, final String value) {
            this.key = key;
            this.value = value;
        }

        public static Pair splitBy(final String content, final String delimiter) {
            String[] header = content.split(delimiter);
            if (header.length != 2) {
                throw new IllegalArgumentException();
            }
            return new Pair(header[0].strip(), header[1].strip());
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
