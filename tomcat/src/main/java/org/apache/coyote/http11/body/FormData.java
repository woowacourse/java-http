package org.apache.coyote.http11.body;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FormData {

    private final Map<String, String> data;

    private FormData(final Map<String, String> data) {
        this.data = data;
    }

    public static FormData of(String body) {
        String[] rawQueries = decode(body).split("&");
        Map<String, String> data = Arrays.stream(rawQueries)
                .collect(Collectors.toMap(
                        rawQuery -> rawQuery.split("=")[0],
                        rawQuery -> rawQuery.split("=")[1]
                ));
        return new FormData(data);
    }

    private static String decode(String urlEncoded) {
        return URLDecoder.decode(urlEncoded, StandardCharsets.UTF_8);
    }

    public String get(String key) {
        return data.getOrDefault(key, "");
    }

    @Override
    public String toString() {
        return "FormData{" +
                "data=" + data +
                '}';
    }
}
