package org.apache.coyote.controller.dynamic;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.error.ErrorCode;
import org.apache.coyote.error.HttpException;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;
import org.apache.coyote.httpRequest.httpHeader.HttpMethod;
import org.apache.coyote.httpResponse.HttpResponse;
import org.apache.coyote.httpResponse.StatusCode;

public class HomeController implements Controller {

    @Override
    public void service(
            final HttpRequest request,
            final HttpResponse response
    ) {
        final HttpHeader httpHeader = request.getHttpHeader();
        final HttpMethod httpMethod = httpHeader.getHttpMethod();
        if (httpMethod.equals(HttpMethod.GET)) {
            doGet(response);
            return;
        }
        throw new HttpException(ErrorCode.NOT_ALLOW_METHOD);
    }

    private void doGet(final HttpResponse response) {
        final String responseBody = "Hello world!";
        response.updateStatusLine("HTTP/1.1", StatusCode.OK);
        response.updateBody(responseBody);
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.addHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }
}
