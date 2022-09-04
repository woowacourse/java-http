package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class HttpRequestHeader {

    private static final String KEY_VALUE_BOUNDARY = ": ";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpRequestHeader(final List<String> requestHeaders) {
        this.values = initValues(requestHeaders);
    }

    private Map<String, String> initValues(final List<String> requestHeaders) {
        if (requestHeaders.isEmpty()) {
            return new HashMap<>();
        }

        final Map<String, String> result = new HashMap<>();

        for (final String requestHeader : requestHeaders) {
            final String[] keyValuePair = requestHeader.split(KEY_VALUE_BOUNDARY);
            result.put(keyValuePair[KEY_INDEX], keyValuePair[VALUE_INDEX]);
        }

        return result;
    }

    public int findContentLength() {
        final String contentLength = values.get(CONTENT_LENGTH);
        if (StringUtils.isBlank(contentLength)) {
            return 0;
        }
        return Integer.parseInt(contentLength);
    }
}
