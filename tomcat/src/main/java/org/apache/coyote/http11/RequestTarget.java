package org.apache.coyote.http11;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.SubstringGenerator;

public class RequestTarget {

    private final String value;

    public RequestTarget(String value) {
        this.value = value;
    }

    public Map<String, String> parseQueryString() {
        if (containsQueryParameter()) {
            String query = SubstringGenerator.splitByFirst("?", value).getLast();
            return parseQueryParameters(query);
        }
        throw new IllegalArgumentException("invalid query string: " + value);
    }

    public boolean containsQueryParameter() {
        return value.contains("?");
    }

    private Map<String, String> parseQueryParameters(String query) {
        String[] parameters = query.split("&");
        Map<String, String> queries = new HashMap<>();
        for (String parameter : parameters) {
            List<String> keyAndValue = SubstringGenerator.splitByFirst("=", parameter);
            queries.put(keyAndValue.getFirst(), keyAndValue.getLast());
        }
        return queries;
    }

    public boolean startsWith(String startsWith) {
        return value.startsWith(startsWith);
    }

    public String getTargetExtension() {
        if (value.contains(".")) {
            return SubstringGenerator.splitByLast(".", value).getLast();
        }
        return "html";
    }

    public URL getUrl() {
        String path = value;
        if (!path.contains(".")) {
            path = path + ".html";
        }
        return getClass().getClassLoader().getResource("static" + path);
    }

    public boolean isBlank() {
        return value.isBlank();
    }
}
