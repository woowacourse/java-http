package org.apache.coyote.http11.cookie;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CookieRequestHeader {

    private static final Logger log = LoggerFactory.getLogger(CookieRequestHeader.class);

    private static final String JAVA_SESSION_ID = "JSESSIONID";
    private static final String DELIMITER = "; ";
    private static final String KEY_VALUE_SPLITTER = "=";

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int DATA_SIZE = 2;
    private static final int KEY_VALUE_SPLIT_LIMIT = 2;
    private static final String EMPTY_VALUE = "";

    private final Map<String, String> data;

    private CookieRequestHeader(final Map<String, String> data) {
        this.data = data;
    }

    public static CookieRequestHeader from(final String value) {
        log.debug("Cookie: {}", value);
        final Map<String, String> data = Arrays.stream(value.split(DELIMITER))
                .map(cookieValue -> cookieValue.split(KEY_VALUE_SPLITTER, KEY_VALUE_SPLIT_LIMIT))
                .collect(Collectors.toMap(
                        oneData -> oneData[KEY_INDEX].trim(),
                        CookieRequestHeader::getDataValueOrBlank,
                        (existing, replacement) -> existing
                ));
        return new CookieRequestHeader(data);
    }

    private static String getDataValueOrBlank(final String[] oneData) {
        if (oneData.length >= DATA_SIZE) {
            return oneData[VALUE_INDEX].trim();
        }
        return EMPTY_VALUE;
    }

    public static CookieRequestHeader blank() {
        return new CookieRequestHeader(new HashMap<>());
    }

    public boolean hasJSessionId() {
        return data.containsKey(JAVA_SESSION_ID);
    }

    public String getJSessionId() {
        return data.get(JAVA_SESSION_ID);
    }
}
