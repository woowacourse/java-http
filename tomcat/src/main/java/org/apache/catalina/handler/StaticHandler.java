package org.apache.catalina.handler;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.StaticResourceUtils;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpResponse;


public class StaticHandler implements HttpHandler {

    private static final List<String> REQUEST_PATHS = List.of(
            "/",
            "/index.html",
            "/css/styles.css",
            "/js/scripts.js",
            "/assets/chart-area.js",
            "/assets/chart-bar.js",
            "/assets/chart-pie.js",
            "/assets/img/error-404-monochrome.svg"
    );

    @Override
    public void handle(final HttpRequest request, final HttpResponse response) {
        getStaticResource(request, response);
    }

    @Override
    public List<String> getAllPath() {
        return new ArrayList<>(REQUEST_PATHS);
    }

    private void getStaticResource(HttpRequest httpRequest, HttpResponse httpResponse) {
        String httpVersion = httpRequest.getHttpVersion();
        String responseBody = StaticResourceUtils.readResourceContent(httpRequest.getRequestPath());
        int statusCode = 200;
        String statusMessage = "OK";
        httpResponse.putHeader("Content-Type", StaticResourceUtils.getContentType(httpRequest.getRequestPath()) + ";charset=utf-8");
        httpResponse.putHeader("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
        httpResponse.update(httpVersion, statusCode, statusMessage, responseBody);
    }
}
