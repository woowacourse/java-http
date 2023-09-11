package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.ContentType.*;

public class RequestBody {

    private static final String AMPERSAND = "&";
    private static final String EQUAL_SIGN = "=";

    private final Map<String, String> queryBodies = new HashMap<>();
    private final String body;
    private final String contentType;

    public RequestBody(String body, String contentType) {
        this.body = body;
        this.contentType = contentType;
        format();
    }

    private void format() {
        if (URL_ENCODED.getType().equals(contentType)) {
            setQueryBody(body);
        }
    }

    private void setQueryBody(String line) {
        String[] split = line.split(AMPERSAND);
        for (String s : split) {
            String[] keyValue = s.split(EQUAL_SIGN);
            queryBodies.put(keyValue[0], keyValue[1]);
        }
    }

    public String getBodyValue(String key){
        return queryBodies.get(key);
    }
}
