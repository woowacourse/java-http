package nextstep.jwp.http.request;

import java.util.HashMap;

public class URI {

    private final String uri;
    private String resourceUri;
    private HashMap<String, String> queryString;

    private URI(String uri, String resourceUri, HashMap<String, String> queryString) {
        this.uri = uri;
        this.resourceUri = resourceUri;
        this.queryString = queryString;
    }

    public static URI of(String uri) {
        HashMap<String, String> queryString = new HashMap<>();
        int index = uri.indexOf("?");
        if (index < 0) {
            return new URI(uri, uri, null);
        }
        String resourceUri = uri.substring(0, index);
        String queries = uri.substring(index + 1);
        if (!queries.isEmpty()) {
            String[] queryStrings = queries.split("&");
            for (String query : queryStrings) {
                String[] values = query.split("=");
                queryString.put(values[0], values[1]);
            }
        }
        return new URI(uri, resourceUri, queryString);
    }

    public String getUri() {
        return uri;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public HashMap<String, String> getQueryString() {
        return queryString;
    }
}
