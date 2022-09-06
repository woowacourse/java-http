package org.apache.coyote.http11;

public class RequestUri {

    private final String resourcePath;
    private final RequestParameters requestParameters;

    private RequestUri(final String resourcePath, final RequestParameters requestParameters) {
        this.resourcePath = resourcePath;
        this.requestParameters = requestParameters;
    }

    public static RequestUri of(final String uri) {
        int index = uri.indexOf("?");
        if (index != -1) {
            return new RequestUri(uri.substring(0, index), RequestParameters.of(uri.substring(index + 1)));
        }
        return new RequestUri(uri, RequestParameters.empty());
    }

    public boolean hasRequestParameters() {
        return !requestParameters.isEmpty();
    }

    public MediaType findMediaType() {
        return MediaType.of(resourcePath);
    }

    public String parseFullPath() {
        if (resourcePath.endsWith(findMediaType().getExtension())) {
            return resourcePath;
        }
        return findMediaType().appendExtension(resourcePath);
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public RequestParameters getRequestParameters() {
        return requestParameters;
    }
}
