package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, String> fields;

    public HttpHeaders() {
        this.fields = new HashMap<>();
    }

    public void add(String headerLine) {
        String[] headerParts = headerLine.split(": ", 2);
        fields.put(headerParts[0], headerParts[1]);
    }

    public int findContentLength() {
        if (fields.containsKey("Content-Length")) {
            return Integer.parseInt(fields.get("Content-Length"));
        }
        return 0;
    }

    public boolean findInCookie(String key) {
        if (fields.containsKey("Cookie")) {
            String value = fields.get("Cookie");

            String[] valueParts = value.split("; ");
            for (String valuePart : valueParts) {
                if (valuePart.startsWith(key + "=")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "fields=" + fields +
                '}';
    }

    public String findJsessionId() {
        if (fields.containsKey("Cookie")) {
            String value = fields.get("Cookie");

            String[] valueParts = value.split("; ");
            for (String valuePart : valueParts) {
                if (valuePart.startsWith("JSESSION=")) {
                    return valuePart;
                }
            }
        }
        return null;
    }
}
