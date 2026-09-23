package org.apache.coyote.http11.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public class StaticResourceController extends AbstractController {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        super.service(request, response);
    }

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) throws Exception {
        String path = request.getPath();
        String resource = getStaticResource(path);
        if (resource != null && response != null) {
            getOkResponse(request, response, resource);
        }
    }

    @Nullable
    private String getStaticResource(String requestUri) throws IOException {
        URL url = getClass().getClassLoader().getResource("static" + requestUri);
        if (url == null) {
            requestUri = requestUri + ".html";
            url = getClass().getClassLoader().getResource("static" + requestUri);
        }
        if (url != null) {
            try (InputStream inputStream = url.openStream()) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            }
        }
        return null;
    }

    private String getContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }

    private void getOkResponse(HttpRequest httpRequest, HttpResponse httpResponse, String responseBody) {
        httpResponse.setVersion(httpRequest.getVersion());
        httpResponse.setStatusCode(200);
        httpResponse.setReasonPhrase("OK");
        httpResponse.setResponseBody(responseBody);
        httpResponse.addHeader("Content-Type", getContentType(httpRequest.getPath()));
        httpResponse.addHeader("Content-Length", responseBody.getBytes(StandardCharsets.UTF_8).length + " ");
    }
}
