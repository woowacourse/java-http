package org.apache.coyote.http11;

import java.net.URL;

public class RequestUrl {

    private final URL url;

    public RequestUrl(String resource) {
        this.url = makeUrl(resource);
    }

    public URL getUrl() {
        return url;
    }

    private URL makeUrl(String resource) {
        return getClass()
                .getClassLoader()
                .getResource("static" + resource);
    }
}
