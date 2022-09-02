package org.apache.coyote.http11.request;

import java.net.URI;

public class Uri {

    private final String path;
    private final Parameters params;

    private Uri(final String path, final Parameters params) {
        this.path = path;
        this.params = params;
    }

    public static Uri parse(final URI uri) {
        final String path = uri.getPath();
        final Parameters params = Parameters.parse(uri.getQuery());
        return new Uri(path, params);
    }

    public String findParam(final String name) {
        return params.find(name);
    }
}
