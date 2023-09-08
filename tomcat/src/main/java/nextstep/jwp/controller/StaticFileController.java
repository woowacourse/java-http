package nextstep.jwp.controller;

import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import java.io.IOException;

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
        return httpRequest.isRequestUriEndsWith(HTML.getFileExtension()) ||
                httpRequest.isRequestUriEndsWith(JS.getFileExtension()) ||
                httpRequest.isRequestUriEndsWith(CSS.getFileExtension()) ||
                httpRequest.isRequestUriEndsWith(ICO.getFileExtension());
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final String filePath = request.getRequestUri().getRequestUri();

        if (request.isRequestUriEndsWith(HTML.getFileExtension())) {
            createHttpResponseByContentTypeAndPath(response, HTML, filePath);
            return;
        }

        if (request.isRequestUriEndsWith(JS.getFileExtension())) {
            createHttpResponseByContentTypeAndPath(response, JS, filePath);
            return;
        }

        if (request.isRequestUriEndsWith(CSS.getFileExtension())) {
            createHttpResponseByContentTypeAndPath(response, CSS, filePath);
            return;
        }

        if (request.isRequestUriEndsWith(ICO.getFileExtension())) {
            createHttpResponseByContentTypeAndPath(response, ICO, filePath);
            return;
        }

        response.mapToRedirect("/404.html");
    }

    private void createHttpResponseByContentTypeAndPath(final HttpResponse response, final ContentType contentType, final String filePath) throws IOException {
        response.changeStatusLine(StatusLine.from(StatusCode.OK));
        response.addHeader(CONTENT_TYPE, contentType.getValue());
        response.changeBody(HttpBody.file(filePath));
    }
}
