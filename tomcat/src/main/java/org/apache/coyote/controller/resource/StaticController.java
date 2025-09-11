package org.apache.coyote.controller.resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.error.ErrorCode;
import org.apache.coyote.error.HttpException;
import org.apache.coyote.httpRequest.HttpRequest;
import org.apache.coyote.httpRequest.httpHeader.HttpHeader;
import org.apache.coyote.httpRequest.httpHeader.HttpMethod;
import org.apache.coyote.httpResponse.HttpResponse;
import org.apache.coyote.httpResponse.StatusCode;

public class StaticController implements Controller {

    private static final StaticResourceReader staticResourceReader = StaticResourceReader.getInstance();

    @Override
    public void service(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException {
        final HttpHeader httpHeader = request.getHttpHeader();
        final HttpMethod httpMethod = httpHeader.getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            responseStaticFile(httpHeader, response);
            return;
        }
        throw new HttpException(ErrorCode.NOT_ALLOW_METHOD);
    }

    private void responseStaticFile(
            final HttpHeader httpHeader,
            final HttpResponse httpResponse
    ) throws IOException {
        final String path = httpHeader.getPurePath();
        final String body = staticResourceReader.getStaticResponseBody("static" + path);
        httpResponse.updateStatusLine("HTTP/1.1", StatusCode.OK);
        httpResponse.updateBody(body);
        httpResponse.addHeader("Content-Type", ResourceType.findResourceType(path).getType());
        httpResponse.addHeader("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }
}
