package org.apache.coyote.http11.httpmessage.request.requestbody;

import java.util.HashMap;
import java.util.Map;

public class RequestBodyContent {

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private static final String CONTENT_SPLITTER = "&";
    private static final String NAME_AND_VALUE_SPLITTER = "=";

    private final Map<String, String> body;

    public RequestBodyContent(final Map<String, String> body) {
        this.body = body;
    }

    public static RequestBodyContent parse(final String requestBody) {
        final Map<String, String> bodyContents = new HashMap<>();

        for (final String queryString : requestBody.split(CONTENT_SPLITTER)) {
            final String name = queryString.split(NAME_AND_VALUE_SPLITTER)[NAME_INDEX];
            final String value = queryString.split(NAME_AND_VALUE_SPLITTER)[VALUE_INDEX];
            bodyContents.put(name, value);
        }
        return new RequestBodyContent(new HashMap<>(bodyContents));
    }

    public String get(final String key) {
        return body.get(key);
    }

    public String getValue(final String key) {
        if (body.containsKey(key)) {
            return body.get(key);
        }
        throw new IllegalArgumentException("존재하지 않는 body내용 입니다.");
    }
}
