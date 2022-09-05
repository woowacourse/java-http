package org.apache.coyote.http11;

public class RequestURI {

    private final String path;
    private final RequestParameters requestParameters;

    public RequestURI(final String requestURI) {
        if (!requestURI.contains("?")) {
            this.path = requestURI;
            this.requestParameters = RequestParameters.EMPTY_PARAMETERS;
            return;
        }

        int index = requestURI.indexOf("?");
        this.path = requestURI.substring(0, index);
        this.requestParameters = new RequestParameters(requestURI.substring(index + 1));
    }

    public String getQueryParameterKey(final String key) {
        return requestParameters.getParameter(key);
    }

    public String getPath() {
        return path;
    }
}
