package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class HttpRequestBody {

    private static final String QUERY_STRINGS_BOUNDARY = "&";
    private static final String KEY_VALUE_BOUNDARY = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> value;

    public HttpRequestBody(final String messageBody) {
        this.value = extractMessageBody(messageBody);
    }

    private Map<String, String> extractMessageBody(final String messageBody) {
        if (StringUtils.isBlank(messageBody)) {
            return new HashMap<>();
        }

        final Map<String, String> result = new HashMap<>();
        final String[] messageBodyInfos = messageBody.split(QUERY_STRINGS_BOUNDARY);

        for (String messageBodyInfo : messageBodyInfos) {
            String[] keyValuePair = messageBodyInfo.split(KEY_VALUE_BOUNDARY);
            result.put(keyValuePair[KEY_INDEX], keyValuePair[VALUE_INDEX]);
        }

        return result;
    }

    public String findByKey(final String key) {
        return value.get(key);
    }
}
