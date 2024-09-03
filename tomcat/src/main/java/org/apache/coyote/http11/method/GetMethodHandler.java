package org.apache.coyote.http11.method;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public class GetMethodHandler {

    private static final String ROOT_PATH = "/";
    private static final String INDEX_FILE = "/index.html";
    private static final String STATIC_PATH = "static";

    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        String requestURI = httpRequest.getRequestURI();

        if (isIndexPageRequest(requestURI)) {
            return handleStaticFile(INDEX_FILE);
        }
        return handleStaticFile(requestURI);
    }

    private boolean isIndexPageRequest(String requestURI) {
        return ROOT_PATH.equals(requestURI) || INDEX_FILE.equals(requestURI);
    }

    private HttpResponse handleStaticFile(String requestURI) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(STATIC_PATH + requestURI);

        if (inputStream == null) {
            return HttpResponse.status(HttpStatus.NOT_FOUND).build();
        }

        String responseBody = new String(inputStream.readAllBytes());

        return HttpResponse.status(HttpStatus.OK)
                .header("Content-Type", "text/html;charset=utf-8")
                .header("Content-Length", String.valueOf(responseBody.getBytes().length))
                .body(responseBody)
                .build();
    }

}

