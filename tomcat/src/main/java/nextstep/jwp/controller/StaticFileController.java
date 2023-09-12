package nextstep.jwp.controller;

import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import java.io.IOException;
import java.util.Arrays;

import static org.apache.coyote.http.common.ContentType.CSS;
import static org.apache.coyote.http.common.ContentType.HTML;
import static org.apache.coyote.http.common.ContentType.ICO;
import static org.apache.coyote.http.common.ContentType.JS;
import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class StaticFileController extends RequestController {

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return isStaticFile(httpRequest);
    }

    private static boolean isStaticFile(final HttpRequest httpRequest) {
        return httpRequest.isEndsWithRequestUri(HTML.getFileExtension()) ||
                httpRequest.isEndsWithRequestUri(JS.getFileExtension()) ||
                httpRequest.isEndsWithRequestUri(CSS.getFileExtension()) ||
                httpRequest.isEndsWithRequestUri(ICO.getFileExtension());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        final String filePath = request.getRequestUri().getRequestUri();

        Arrays.stream(ContentType.values())
                .filter(contentType -> request.isEndsWithRequestUri(contentType.getFileExtension()))
                .findAny()
                .ifPresentOrElse(
                        contenttype -> createHttpResponse(response, contenttype, filePath),
                        () -> response.mapToRedirect("/404.html")
                );
    }

    private void createHttpResponse(final HttpResponse response, final ContentType contenttype, final String filePath) {
        try {
            createHttpResponseByContentTypeAndPath(response, contenttype, filePath);
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void createHttpResponseByContentTypeAndPath(final HttpResponse response, final ContentType contentType, final String filePath) throws IOException {
        response.changeStatusLine(StatusLine.from(StatusCode.OK));
        response.addHeader(CONTENT_TYPE, contentType.getValue());
        response.changeBody(HttpBody.file(filePath));
    }
}
