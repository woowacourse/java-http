package org.apache.coyote.http11.domain;

import java.io.IOException;
import java.io.InputStream;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.response.ContentTypeResolver;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.domain.response.HttpStatus;

public class StaticResourceHandler {

    private static final String ROOT_PATH = "/";
    private static final String INDEX_FILE = "/index.html";
    private static final String STATIC_PATH = "static";

    public HttpResponse handle(HttpRequest httpRequest) throws IOException {
        String requestURI = httpRequest.getRequestURI();

        if (isIndexPageRequest(requestURI)) {
            return handleStaticResource(INDEX_FILE);
        }
        return handleStaticResource(requestURI);
    }

    private boolean isIndexPageRequest(String requestURI) {
        return ROOT_PATH.equals(requestURI) || INDEX_FILE.equals(requestURI);
    }

    private HttpResponse handleStaticResource(String requestURI) throws IOException {
        String staticFilePath = STATIC_PATH + requestURI;
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(staticFilePath);

        if (inputStream == null) {
            return HttpResponse.status(HttpStatus.NOT_FOUND).build();
        }

        String responseBody = new String(inputStream.readAllBytes());

        return HttpResponse.status(HttpStatus.OK)
                .contentType(ContentTypeResolver.getContentType(staticFilePath))
                .contentLength(String.valueOf(responseBody.getBytes().length))
                .body(responseBody)
                .build();
    }

}

