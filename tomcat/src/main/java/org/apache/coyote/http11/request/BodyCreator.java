package org.apache.coyote.http11.request;

import util.BiValue;
import util.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class BodyCreator {
    public static final RequestBody EMPTY_BODY = new RequestBody(Map.of());

    public static RequestBody create(final String contentType, final String text) {
        return createFormUrlencoded(text);
    }

    private static RequestBody createFormUrlencoded(final String text) {
        return new RequestBody(Arrays.stream(text.split("&"))
                .map(s -> StringUtil.splitBiValue(s, "="))
                .collect(Collectors.toMap(BiValue::first, BiValue::second)));
    }

    private BodyCreator() {}
}
