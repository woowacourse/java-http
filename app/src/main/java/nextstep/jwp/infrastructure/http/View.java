package nextstep.jwp.infrastructure.http;

import java.util.Objects;
import nextstep.jwp.infrastructure.http.response.HttpResponse;
import nextstep.jwp.infrastructure.http.response.HttpStatusCode;

public class View {

    private final HttpStatusCode statusCode;
    private final String resourceName;
    private final HttpResponse response;

    private View(final HttpStatusCode statusCode, final String resourceName, final HttpResponse response) {
        this.statusCode = statusCode;
        this.resourceName = resourceName;
        this.response = response;
    }

    public static View buildByResource(final String resourceName) {
        return buildByResource(HttpStatusCode.OK, resourceName);
    }

    public static View buildByResource(final HttpStatusCode statusCode, final String resourceName) {
        return new View(statusCode, resourceName, null);
    }

    public static View buildByHttpResponse(final HttpResponse response) {
        return new View(null, null, response);
    }

    public boolean needsResource() {
        return Objects.nonNull(resourceName);
    }

    public String getResourceName() {
        return resourceName;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
