package org.apache.coyote.http11.request.startline;

import java.util.Map;

public class Path {

    private static final String PATH_DELIMITER = "?";

    private final String resource;
    private final Extension extension;
    private final QueryParameter queryParameter;

    private Path(String resource, Extension extension, QueryParameter queryParameter) {
        this.resource = resource;
        this.extension = extension;
        this.queryParameter = queryParameter;
    }

    public static Path from(String uri) {
        if (!uri.contains(PATH_DELIMITER)) {
            return new Path(uri, Extension.from(uri), QueryParameter.empty());
        }

        int index = uri.indexOf(PATH_DELIMITER);
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

    public boolean isIcoContentType() {
        return extension.isIco();
    }

    public boolean isStaticResource() {
        return extension != Extension.NONE;
    }
}
