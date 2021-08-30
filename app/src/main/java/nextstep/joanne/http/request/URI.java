package nextstep.joanne.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class URI {
    private final String pathUri;
    private final String resourceUri;
    private final HashMap<String, String> queryString;

    private URI(String pathUri, String resourceUri, Map<String, String> queryString) {
        this.pathUri = pathUri;
        this.resourceUri = resourceUri;
        this.queryString = (HashMap<String, String>) queryString;
    }

    public static URI of(String pathUri) {
        Map<String, String> tempQueryString = queryString(pathUri);
        String resourceUri = pathUri;
        if (Objects.nonNull(tempQueryString)) {
            resourceUri = pathUri.substring(0, pathUri.indexOf("?"));
        }
        resourceUri = toResourceFile(resourceUri);
        return new URI(pathUri, resourceUri, tempQueryString);
    }

    private static String toResourceFile(String resourceUri) {
        if (Objects.equals(resourceUri, "/")) {
            return "/index.html";
        }
        if (!resourceUri.contains(".")) {
            resourceUri += ".html";
        }
        return resourceUri;
    }

    private static Map<String, String> queryString(String uri) {
        int index = uri.indexOf("?");
        if (index < 0) {
            return null;
        }
        String strQueryString = uri.substring(index + 1);
        HashMap<String, String> tempQueryString = new HashMap<>();
        if (!strQueryString.isBlank()) {
            String[] queryStrings = strQueryString.split("&");
            for (String query : queryStrings) {
                String[] values = query.split("=");
                tempQueryString.put(values[0], values[1]);
            }
        }
        return tempQueryString;
    }

    public boolean equalsWith(String other) {
        if (Objects.nonNull(resourceUri)) {
            return Objects.equals(resourceUri, other);
        }
        return Objects.equals(pathUri, other);
    }

    public boolean contains(String uri) {
        if (Objects.nonNull(resourceUri)) {
            return resourceUri.contains(uri);
        }
        return pathUri.contains(uri);
    }

    public String resourceUri() {
        return resourceUri;
    }

    public Map<String, String> queryString() {
        return queryString;
    }

}