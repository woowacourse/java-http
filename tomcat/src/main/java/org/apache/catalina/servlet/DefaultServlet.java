package org.apache.catalina.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.start.HttpMethod;
import org.apache.coyote.http11.request.start.HttpVersion;
import org.apache.coyote.http11.request.start.RequestTarget;
import org.apache.coyote.http11.response.HttpContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import static org.apache.coyote.http11.request.start.HttpMethod.*;

public class DefaultServlet implements Servlet {

    @Override
    public HttpResponse service(final HttpRequest request) throws IOException {
        final HttpVersion httpVersion = request.getHttpStartLine().getHttpVersion();
        validateHttpMethod(request.getHttpStartLine().getHttpMethod());
        final String responseBody = makeResponseBody(request.getHttpStartLine().getRequestTarget());
        return HttpResponse.of(
                httpVersion,
                HttpStatus.OK,
                Map.of("Content-Type", HttpContentType.from(request.getHttpExtension()).getContentType()),
                responseBody
        );
    }

    private String makeResponseBody(final RequestTarget requestTarget) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(
                "static/" + requestTarget.getPath() + requestTarget.getExtensionName());
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private void validateHttpMethod(final HttpMethod httpMethod) {
        if (!httpMethod.equals(GET)) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
    }
}
