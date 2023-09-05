package org.apache.coyote.http11.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CookieHeader {

    private static final Logger log = LoggerFactory.getLogger(CookieHeader.class);

    private static final String JAVA_SESSION_ID = "JSESSIONID";
    private static final String DELIMITER = "; ";
    private static final String KEY_VALUE_SPLITTER = "=";
    private static final int KEY_VALUE_SPLIT_LIMIT = 2;
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int DATA_SIZE = 2;
    private static final String EMPTY_VALUE = "";

    private final Map<String, String> data;

    private CookieHeader(final Map<String, String> data) {
        this.data = data;
    }

    public static CookieHeader from(final String value) {
        log.debug("Cookie: {}", value);
        final Map<String, String> data = Arrays.stream(value.split(DELIMITER))
                .map(cookieValue -> cookieValue.split(KEY_VALUE_SPLITTER, KEY_VALUE_SPLIT_LIMIT))
                .collect(Collectors.toMap(
                        oneData -> oneData[KEY_INDEX].trim(),
                        CookieHeader::getDataValueOrBlank
                ));
        return new CookieHeader(data);
    }

    private static String getDataValueOrBlank(final String[] oneData) {
        if (oneData.length > DATA_SIZE) {
            return oneData[VALUE_INDEX].trim();
        }
        return EMPTY_VALUE;
    }

    public static CookieHeader blank() {
        return new CookieHeader(new HashMap<>());
    }

    public static CookieHeader createByJSessionId(final String jSessionId) {
        final Map<String, String> data = new HashMap<>();
        data.put(JAVA_SESSION_ID, jSessionId);
        return new CookieHeader(data);
    }

    public boolean isExist() {
        return !data.isEmpty();
    }

    public boolean hasJSessionId() {
        return data.containsKey(JAVA_SESSION_ID);
    }

    public String getJSessionId() {
        if (!hasJSessionId()) {
            throw new RuntimeException("JSESSIONID 가 존재하지 않는데, JSESSIONID 를 가져오려 합니다.");
        }
        return data.get(JAVA_SESSION_ID);
    }

    public String getValue() {
        return data.entrySet().stream()
                .map(entry -> entry.getKey() + KEY_VALUE_SPLITTER + entry.getValue())
                .collect(Collectors.joining(DELIMITER));
    }
}
