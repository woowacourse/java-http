package org.apache.coyote.http11;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private final URL url;
    private final String extension;
    private final Map<String, String> queryString;

    public Request(final URL url, final String extension, final Map<String, String> queryString) {
        this.url = url;
        this.extension = extension;
        this.queryString = queryString;
    }

    public Request(final String path) {
        if (path.contains("?")) {
            this.url = getClass().getClassLoader()
                    .getResource("static" + path.substring(0, path.indexOf("?")) + ".html");
            final var query = path.substring(path.indexOf('?') + 1);
            this.queryString = new HashMap<>();
            final String[] queryParams = query.split("&");
            for (final String param : queryParams) {
                final var pair = param.split("=");
                this.queryString.put(pair[0], pair[1]);
            }
            this.extension = "html";
            return;
        }
        if (path.contains(".")) {
            this.url = getClass().getClassLoader().getResource("static" + path);
            this.extension = path.substring(path.lastIndexOf(".") + 1);
            this.queryString = new HashMap<>();
            return;
        }
        this.url = getClass().getClassLoader().getResource("static" + path + ".html");
        this.extension = "html";
        this.queryString = new HashMap<>();
    }

    public String getContentType() {
        if (extension != null) {
            return "text/" + extension;
        }
        return "text/html";
    }

    public URL getUrl() {
        return url;
    }

    public String getExtension() {
        return extension;
    }

    public Map<String, String> getQueryString() {
        return Collections.unmodifiableMap(queryString);
    }

    @Override
    public String toString() {
        return "Request{" +
               "url=" + url +
               ", extension='" + extension + '\'' +
               ", queryString=" + queryString +
               '}';
    }
}
