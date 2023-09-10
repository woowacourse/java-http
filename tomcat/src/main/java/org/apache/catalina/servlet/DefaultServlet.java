package org.apache.catalina.servlet;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.HttpHeadersLine;
import org.apache.coyote.http11.request.start.HttpVersion;
import org.apache.coyote.http11.request.start.RequestTarget;
import org.apache.coyote.http11.response.HttpContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http11.request.start.HttpMethod.*;

public class DefaultServlet implements Servlet {

    @Override
    public void service(final HttpRequest request, final HttpResponse response) throws IOException {
        final HttpVersion httpVersion = request.getHttpStartLine().getHttpVersion();
        if (!request.getHttpMethod().equals(GET)) {
            response.setHeader(httpVersion, HttpStatus.FOUND, HttpHeadersLine.initHeaders().getHeaders());
            response.setRedirect("404.html");
            return;
        }
        final String responseBody = makeResponseBody(request.getHttpStartLine().getRequestTarget());
        response.setHeader(httpVersion, HttpStatus.OK, makeResponseHeader(request));
        response.setBody(responseBody);
    }

    private Map<String, String> makeResponseHeader(final HttpRequest request) {
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", HttpContentType.from(request.getHttpExtension()).getContentType());
        return header;
    }

    private String makeResponseBody(final RequestTarget requestTarget) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(
                "static/" + requestTarget.getPath() + requestTarget.getExtensionName());
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }
}
