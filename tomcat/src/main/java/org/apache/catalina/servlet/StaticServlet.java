package org.apache.catalina.servlet;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.catalina.StaticResourceUtils;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StaticServlet extends AbstractServlet {

    private static final Logger log = LoggerFactory.getLogger(StaticServlet.class);

    @Override
    public boolean possibleHandle(final String requestPath) {
        return Objects.equals(requestPath, "/") || requestPath.contains(".");
    }

    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) {
        String httpVersion = request.getHttpVersion();
        String responseBody = StaticResourceUtils.readResourceContent(request.getRequestPath());
        int statusCode = 200;
        String statusMessage = "OK";
        response.putHeader("Content-Type", StaticResourceUtils.getContentType(request.getRequestPath()) + ";charset=utf-8");
        response.putHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        response.update(httpVersion, statusCode, statusMessage, responseBody);
    }

    @Override
    public void doPost(final HttpRequest request, final HttpResponse response) {
        throw new IllegalArgumentException("처리할 수 없습니다.");
    }
}
