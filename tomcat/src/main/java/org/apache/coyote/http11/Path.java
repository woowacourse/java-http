package org.apache.coyote.http11;

import java.util.Map;

public class Path {

    private final String resource;
    private final Extension extension;
    private final QueryParameter queryParameter;

    private Path(String resource, Extension extension, QueryParameter queryParameter) {
        this.resource = resource;
        this.extension = extension;
        this.queryParameter = queryParameter;
    }

    public static Path from(String uri) {
        if (!uri.contains("?")) {
            return new Path(uri, Extension.from(uri), QueryParameter.from(null));
        }

        int index = uri.indexOf("?");
        String resource = uri.substring(0, index);
        String queryParameter = uri.substring(index + 1);

        return new Path(resource, Extension.from(resource), QueryParameter.from(queryParameter));
    }

    public String getResource() {
        return resource;
    }

    public String getContentType() {
        return extension.getContentType();
    }

    public Map<String, String> getQueryParameter() {
        return queryParameter.getParams();
    }
}
