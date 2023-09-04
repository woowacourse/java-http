package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class RequestForms {
    private final Map<String, String> requestForms;

    public RequestForms(final Map<String, String> requestForms) {
        this.requestForms = requestForms;
    }

    public static RequestForms from(final String values) {
        final Map<String, String> requestForms = new LinkedHashMap<>();

        final String[] splitedValues = values.split("&");
        for (final String value : splitedValues) {
            final String[] formPair = value.split("=");
            requestForms.put(formPair[0], formPair[1]);
        }
        return new RequestForms(requestForms);
    }

    public Map<String, String> getRequestForms() {
        return requestForms;
    }
}
