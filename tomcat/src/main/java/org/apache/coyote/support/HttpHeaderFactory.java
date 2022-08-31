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

        private final HttpHeader key;
        private final String value;

        public Pair(final HttpHeader key, final String value) {
            this.key = key;
            this.value = value;
        }

        public HttpHeader getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }
}
