package org.apache.coyote.http11.httpRequest;

import java.util.Map;

public class Uri {

    private final String path;
    private final Params params;

    private Uri(
            final String path,
            final Params params
    ) {
        this.path = path;
        this.params = params;
    }

    public static Uri parse(final String uri) {
        String path = uri;
        Params params = Params.empty();

        if (uri.contains("?")) {
            final int index = uri.indexOf("?");
            path = uri.substring(0, index);

            final String queryString = uri.substring(index + 1);
            params = Params.parse(queryString);
        }

        return new Uri(path, params);
    }

    public String getPath() {
        return this.path;
    }

    public Map<String, String> getParams() {
        return this.params.getParams();
    }
}
