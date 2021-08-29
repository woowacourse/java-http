package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class Uri {

    private final String resourceUri;
    private final Map<String, String> queryString;

    public Uri(String resourceUri, Map<String, String> queryString) {
        this.resourceUri = resourceUri;
        this.queryString = queryString;
    }

    public static Uri valueOf(@Nonnull String pullUri) {
        int index = pullUri.indexOf("?");
        if (index == -1) {
            return new Uri(pullUri, new HashMap<>());
        }
        Map<String, String> queryString = new HashMap<>();
        String uri = pullUri.substring(0, index);
        for (String query : pullUri.substring(index + 1).split("&")) {
            String[] keyValue = query.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            queryString.put(key, value);
        }
        return new Uri(uri, queryString);
    }

    public String getQuery(String key) {
        return queryString.get(key);
    }

    public boolean isUriFile() {
        return resourceUri.endsWith(".html")
            || resourceUri.endsWith(".css")
            || resourceUri.endsWith(".js");
    }

    public String getResourceUri() {
        return resourceUri;
    }
}
