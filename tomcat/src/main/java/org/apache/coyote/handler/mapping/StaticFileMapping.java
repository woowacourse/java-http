package org.apache.coyote.handler.mapping;

import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeaders;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.ContentType;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;

public class StaticFileMapping implements HandlerMapping {

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.isGetRequest() &&
                ("/".equals(httpRequest.getRequestUri().getRequestUri()) ||
                        "/index.html".equals(httpRequest.getRequestUri().getRequestUri()) ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".js") ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".css") ||
                        httpRequest.getRequestUri().getRequestUri().endsWith(".ico")
                );
    }

    @Override
    public String handle(final HttpRequest httpRequest) throws IOException {
        final String requestUri = httpRequest.getRequestUri().getRequestUri();
        if ("/".equals(requestUri)) {
            final var responseBody = "Hello world!";

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if ("/index.html".equals(requestUri)) {
            final String filePath = "static" + requestUri;
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (requestUri.endsWith(".js")) {
            final String filePath = "static" + requestUri;
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/javascript ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (requestUri.endsWith(".css")) {
            final String filePath = "static" + requestUri;
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        if (requestUri.endsWith(".ico")) {
            final String filePath = "static" + requestUri;
            final URL fileUrl = getClass().getClassLoader().getResource(filePath);
            final Path path = new File(fileUrl.getPath()).toPath();
            final String responseBody = new String(Files.readAllBytes(path));

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: image/x-icon ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }

        return null;
    }

    @Override
    public HttpResponse handle2(final HttpRequest httpRequest) throws IOException {
        final String requestUri = httpRequest.getRequestUri().getRequestUri();
        if ("/".equals(requestUri)) {
            final var responseBody = "Hello world!";

            final HttpResponse response = HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.HTML.getValue())))
                    .body(new HttpBody("Hello world!"))
                    .build();

            return response;
        }

        if ("/index.html".equals(requestUri)) {
            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.HTML.getValue())))
                    .body(HttpBody.file("static/index.html"))
                    .build();
        }

        if (requestUri.endsWith(".js")) {
            final String filePath = "static" + requestUri;

            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.JS.getValue())))
                    .body(HttpBody.file(filePath))
                    .build();
        }

        if (requestUri.endsWith(".css")) {
            final String filePath = "static" + requestUri;

            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.CSS.getValue())))
                    .body(HttpBody.file(filePath))
                    .build();
        }

        if (requestUri.endsWith(".ico")) {
            final String filePath = "static" + requestUri;

            return HttpResponse.builder()
                    .statusLine(StatusLine.from(StatusCode.OK))
                    .httpHeaders(new HttpHeaders(Map.of(CONTENT_TYPE, ContentType.ICO.getValue())))
                    .body(HttpBody.file(filePath))
                    .build();
        }

        return null;
    }
}
