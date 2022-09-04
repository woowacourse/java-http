package org.apache.coyote.http11.request;

import java.net.URI;

public class Uri {

    private final String path;
    private final Params params;

    private Uri(final String path, final Params params) {
        this.path = path;
        this.params = params;
    }

    public static Uri parse(final URI uri) {
        final String path = uri.getPath();
        final Params params = Params.parse(uri.getQuery());
        return new Uri(path, params);
    }

    public String findParam(final String name) {
        return params.find(name);
    }

    public String getPath() {
        return path;
    }

    public Params getParams() {
        return params;
    }
}
