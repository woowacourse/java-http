package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RequestBody {

    private static final String EMPTY_VALUE = "";
    private static final String PARAM_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private final String value;

    private RequestBody(final String value) {
        this.value = value;
    }

    public static RequestBody of(final BufferedReader reader, final HttpHeaders headers) throws IOException {
        final int contentLength = headers.getContentLength();
        if (contentLength == 0) {
            return new RequestBody(EMPTY_VALUE);
        }
        final char[] buffer = new char[contentLength];
        reader.read(buffer);
        return new RequestBody(new String(buffer));
    }

    public Map<String, String> toParams() {
        final Map<String, String> params = new HashMap<>();
        for (final String parameter : value.split(PARAM_SEPARATOR)) {
            final String[] keyAndValue = parameter.split(KEY_VALUE_SEPARATOR, 2);
            if (keyAndValue.length == 2) {
                params.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return params;
    }
}
