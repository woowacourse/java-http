package nextstep.joanne.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class URI {
    private final String uri;
    private String resourceUri;
    private HashMap<String, String> queryString;

    public URI(String uri) {
        this.uri = uri;
        this.queryString = (HashMap<String, String>) queryString(uri);

        if (Objects.isNull(queryString)) {
            resourceUri = uri;
        }
    }

    private Map<String, String> queryString(String uri) {
        int index = uri.indexOf("?");
        if (index < 0) {
            return null;
        }

        this.resourceUri = uri.substring(0, index);
        String strQueryString = uri.substring(index + 1);

        queryString = new HashMap<>();
        if (!strQueryString.isBlank()) {
            String[] queryStrings = strQueryString.split("&");
            for (String query : queryStrings) {
                String[] values = query.split("=");
                queryString.put(values[0], values[1]);
            }
        }

        return queryString;
    }

    public boolean contains(String other) {
        return uri.contains(other);
    }

    public String resourceUri() {
        return resourceUri;
    }

    public Map<String, String> queryString() {
        return queryString;
    }
}
