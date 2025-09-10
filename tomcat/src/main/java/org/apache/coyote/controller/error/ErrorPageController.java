package org.apache.coyote.controller.error;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.resource.StaticResourceReader;
import org.apache.coyote.error.ErrorPage;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;
import org.apache.coyote.httpResponse.HttpResponse;

public class ErrorPageController implements Controller {

    private static final StaticResourceReader staticResourceReader = StaticResourceReader.getInstance();

    @Override
    public void service(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException {
        final HttpHeader httpHeader = request.getHttpHeader();
        String path = httpHeader.getPurePath();
        responseErrorPage(path, response);
    }

    private void responseErrorPage(
            final String errorPagePath,
            final HttpResponse httpResponse
    ) throws IOException {
        final String body = staticResourceReader.getStaticResponseBody("static" + errorPagePath);
        httpResponse.updateStatusLine("HTTP/1.1", ErrorPage.findStatusCode(errorPagePath));
        httpResponse.updateBody(body);
        httpResponse.addHeader("Content-Type", "text/html;charset=utf-8");
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }
}
