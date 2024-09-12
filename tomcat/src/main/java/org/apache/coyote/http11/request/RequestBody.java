package org.apache.coyote.http11.request;

import java.util.Map;

import org.apache.coyote.http11.MapFactory;

public class RequestBody {
    private static final String ELEMENT_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    private String body;

    public RequestBody() {
        this.body = "";
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getUserInformation() {
        return MapFactory.create(body, ELEMENT_SEPARATOR, KEY_VALUE_SEPARATOR);
    }
}
