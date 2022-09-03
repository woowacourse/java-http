package org.apache.coyote.http11.request;

import java.util.Map;

public class Path {

    private static final String PATH_DELIMITER = "?";
    private static final String EXTENSION_DELIMITER = ".";

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
            return new Path(removeExtension(uri), Extension.from(uri), QueryParameter.empty());
        }

        int index = uri.indexOf(PATH_DELIMITER);
        String resource = uri.substring(0, index);
        String queryParameter = uri.substring(index + 1);

        return new Path(removeExtension(resource), Extension.from(resource), QueryParameter.from(queryParameter));
    }

    private static String removeExtension(String uri) {
        if (uri.contains(EXTENSION_DELIMITER)) {
            return uri.split("\\" + EXTENSION_DELIMITER)[0];
        }
        return uri;
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

    public String getExtension() {
        return extension.getExtension();
    }

    public boolean isIcoContentType() {
        return extension.isIco();
    }
}
