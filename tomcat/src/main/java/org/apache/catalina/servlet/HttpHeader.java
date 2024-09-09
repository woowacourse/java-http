package org.apache.catalina.servlet;

import java.util.ArrayList;
import java.util.List;

public class HttpHeader {

    private final String key;
    private final List<String> values;

    public HttpHeader(final String key, final List<String> values) {
        this.key = key;
        this.values = values;
    }

    public String getKey() {
        return key;
    }

    public List<String> getValues() {
        return new ArrayList<>(values);
    }
}
