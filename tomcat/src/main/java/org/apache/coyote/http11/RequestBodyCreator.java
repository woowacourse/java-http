package org.apache.coyote.http11;

import org.apache.coyote.http11.request.RequestBody;
import util.BiValue;
import util.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestBodyCreator {
    public static final RequestBody EMPTY_BODY = new RequestBody(Map.of());

    private static final String FORM_URL_ENCODED_DELIMITER = "&";
    private static final String FORM_URL_ENCODED_BODY_DELIMITERS = "=";

    public static RequestBody create(final String contentType, final String text) {
        return createFormUrlencoded(text);
    }

    private static RequestBody createFormUrlencoded(final String text) {
        return new RequestBody(Arrays.stream(text.split(FORM_URL_ENCODED_DELIMITER))
                .map(s -> StringUtil.splitBiValue(s, FORM_URL_ENCODED_BODY_DELIMITERS))
                .collect(Collectors.toMap(BiValue::first, BiValue::second)));
    }

    private RequestBodyCreator() {}
}
