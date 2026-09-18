package org.apache.coyote.http11.request;

public class HttpRequestTarget {
    private final String path;
    private final HttpQueryParams queryParams;

    public HttpRequestTarget(String target) {
        String[] targetParts = target.split("\\?", 2);

        this.path = targetParts[0];
        this.queryParams = HttpQueryParams.from(targetParts.length == 2 ? targetParts[1] : "");
    }

    public String getPath() {
        return path;
    }

    public String getParams(String key) {
        return queryParams.get(key);
    }
}
