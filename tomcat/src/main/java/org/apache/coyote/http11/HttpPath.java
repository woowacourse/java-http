package org.apache.coyote.http11;

import java.util.Map;

public class HttpPath {

    private final String resource;
    private final HttpExtensionType httpExtensionType;
    private final QueryParameter queryParameter;

    private HttpPath(final String resource, final HttpExtensionType httpExtensionType, final QueryParameter queryParameter) {
        this.resource = resource;
        this.httpExtensionType = httpExtensionType;
        this.queryParameter = queryParameter;
    }

    public static HttpPath from(final String uri) {
        if (!uri.contains("?")) {
            return new HttpPath(removeExtension(uri), HttpExtensionType.from(uri), QueryParameter.empty());
        }

        final int index = uri.indexOf("?");
        final String resource = uri.substring(0, index);
        final String queryParameter = uri.substring(index + 1);

        return new HttpPath(removeExtension(resource), HttpExtensionType.from(resource), QueryParameter.from(queryParameter));
    }

    private static String removeExtension(final String uri) {
        if (uri.contains(".")) {
            return uri.split("\\" + ".")[0];
        }
        return uri;
    }

    public boolean isDefaultResource() {
        return resource.equals("/");
    }

    public boolean isParamEmpty() {
        return queryParameter.getParams().isEmpty();
    }

    public String getResource() {
        return resource;
    }

    public HttpExtensionType getContentType() {
        return httpExtensionType;
    }

    public Map<String, String> getQueryParameter() {
        return queryParameter.getParams();
    }

    public String getExtension() {
        return httpExtensionType.getExtension();
    }
}
