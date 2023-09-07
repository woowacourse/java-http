package org.apache.coyote.http11.response;

import java.io.IOException;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ControllerMapping;
import nextstep.jwp.controller.PageControllerMapping;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private static final String NEW_LINE = "\r\n";

    private final ResponseLine responseLine;
    private final HttpHeaders headers;
    private final String responseBody;

    public HttpResponse(final ResponseLine responseLine, final HttpHeaders headers, final String responseBody) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse parse(final HttpRequest request) throws IOException {
        final Controller controller = ControllerMapping.find(request);
        return controller.process(request);
    }

    @Override
    public String toString() {
        return responseLine.toString() +
                NEW_LINE +
                headers.toString() +
                NEW_LINE +
                responseBody;
    }
}
