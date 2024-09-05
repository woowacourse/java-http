package org.apache.coyote.http11;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Path {

    private final String value;
    private final URL url;
    private final String extension;
    private final Map<String, String> queryString; //TODO request Param?

    public Path(final String target) {
        this.value = target;
        if (target.contains("?")) {
            this.url = getClass().getClassLoader()
                    .getResource("static" + target.substring(0, target.indexOf("?")) + ".html");
            final var query = target.substring(target.indexOf('?') + 1);
            this.queryString = new HashMap<>();
            final String[] queryParams = query.split("&");
            for (final String param : queryParams) {
                final var pair = param.split("=");
                this.queryString.put(pair[0], pair[1]);
            }
            this.extension = "html";
            return;
        }
        if (target.contains(".")) {
            this.url = getClass().getClassLoader().getResource("static" + target);
            this.extension = target.substring(target.lastIndexOf(".") + 1);
            this.queryString = new HashMap<>();
            return;
        }
        this.url = getClass().getClassLoader().getResource("static" + target + ".html");
        this.extension = "html";
        this.queryString = new HashMap<>();
    }

    public String getContentType() {
        if (extension != null) {
            return "text/" + extension;
        }
        return "text/html";
    }

    public String getValue() {
        return value;
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
        return "Path{" +
               "value=" + value +
               ", extension='" + extension + '\'' +
               ", queryString=" + queryString +
               '}';
    }
}
