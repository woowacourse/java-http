package org.apache.coyote.http11;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Path {

    private final String value;
    private final URL url;
    private final String extension;
    private final Map<String, String> parameters;

    public Path(final String target) {
        this.value = target;
        if (target.contains("?")) {
            this.url = getClass().getClassLoader()
                    .getResource("static" + target.substring(0, target.indexOf("?")) + ".html");
            this.parameters = new HashMap<>();
            final var query = target.substring(target.indexOf('?') + 1);
            final var queryParams = query.split("&");
            for (final var param : queryParams) {
                final var pair = param.split("=");
                this.parameters.put(pair[0], pair[1]);
            }
            this.extension = "html";
            return;
        }
        if (target.contains(".")) {
            this.url = getClass().getClassLoader().getResource("static" + target);
            this.extension = target.substring(target.lastIndexOf(".") + 1);
            this.parameters = new HashMap<>();
            return;
        }
        this.url = getClass().getClassLoader().getResource("static" + target + ".html");
        this.extension = "html";
        this.parameters = new HashMap<>();
    }

    public boolean isEqualPath(final String target) {
        return Objects.equals(value, target);
    }

    public String getContentType() { //TODO extension이 여기에 있을 필요 무
        return extension;
    }

    public String getValue() {
        return value;
    }

    public URL getUrl() {
        return url;
    }

    public Map<String, String> getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    @Override
    public String toString() {
        return "Path{" +
               "value=" + value +
               ", extension='" + extension + '\'' +
               ", queryString=" + parameters +
               '}';
    }
}
