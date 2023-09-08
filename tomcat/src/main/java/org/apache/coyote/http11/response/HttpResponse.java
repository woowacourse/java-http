package org.apache.coyote.http11.response;

import java.io.IOException;
import java.nio.file.Path;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ControllerMapping;
import nextstep.jwp.controller.page.InternalServerErrorController;
import nextstep.jwp.util.ResponseBodyUtil;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;

public class HttpResponse {

    private static final String NEW_LINE = "\r\n";

    private final ResponseStatusLine responseStatusLine;
    private final HttpHeaders headers;
    private final String responseBody;

    public HttpResponse(final ResponseStatusLine responseStatusLine, final HttpHeaders headers,
                        final String responseBody) {
        this.responseStatusLine = responseStatusLine;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse create(final HttpStatus httpStatus, final Path path) throws IOException {
        final ResponseStatusLine statusLine = ResponseStatusLine.create(httpStatus);
        final HttpHeaders headers = HttpHeaders.createResponse(path);
        final String responseBody = ResponseBodyUtil.alter(path);

        return new HttpResponse(statusLine, headers, responseBody);
    }

    public static HttpResponse createRedirect(final Path path, final String redirectUri)
            throws IOException {
        final ResponseStatusLine statusLine = ResponseStatusLine.create(HttpStatus.FOUND);
        final HttpHeaders headers = HttpHeaders.createResponse(path);
        headers.setHeader(HttpHeaders.LOCATION, redirectUri);
        final String responseBody = ResponseBodyUtil.alter(path);

        return new HttpResponse(statusLine, headers, responseBody);
    }

    public static HttpResponse createRedirectWithHeaders(final HttpHeaders headers, final Path path,
                                                         final String redirectUri) throws IOException {
        final ResponseStatusLine statusLine = ResponseStatusLine.create(HttpStatus.FOUND);
        headers.setHeader(HttpHeaders.LOCATION, redirectUri);
        final String responseBody = ResponseBodyUtil.alter(path);

        return new HttpResponse(statusLine, headers, responseBody);
    }

    public static HttpResponse parse(final HttpRequest request) throws IOException {
        try {
            final Controller controller = ControllerMapping.find(request);
            return controller.process(request);
        } catch (final Exception e) {
            return InternalServerErrorController.create(request);
        }
    }

    @Override
    public String toString() {
        return responseStatusLine.toString() +
                NEW_LINE +
                headers.toString() +
                NEW_LINE +
                responseBody;
    }
}
