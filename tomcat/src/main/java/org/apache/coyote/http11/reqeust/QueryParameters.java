package org.apache.coyote.http11.reqeust;

import java.util.HashMap;
import java.util.Map;

public class QueryParameters {

    private final Map<String, String> parameters = new HashMap<>();

    public void addParameter(
            final String name,
            final String value
    ) {
        parameters.put(name, value);
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public boolean containsParam(final String name) {
        return parameters.containsKey(name);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Map.Entry<String, String> entry : parameters.entrySet()) {
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append(", ");
        }

        return sb.toString();
    }
}
