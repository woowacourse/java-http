package nextstep.jwp.infrastructure.http;

import nextstep.jwp.infrastructure.http.response.HttpStatusCode;

public class View {

    private final String resourceName;
    private final HttpStatusCode statusCode;

    public View(final String resourceName, final HttpStatusCode statusCode) {
        this.resourceName = resourceName;
        this.statusCode = statusCode;
    }

    public View(final String resourceName) {
        this(resourceName, HttpStatusCode.OK);
    }

    public String getResourceName() {
        return resourceName;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }
}
