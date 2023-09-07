package org.apache.coyote.http11.response;

import lombok.Getter;
import org.apache.coyote.http11.session.HttpCookie;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

@Getter
public class HttpResponse {

    private static final String DELIMITER = "\r\n";
    private static final String BLANK_LINE = "";
    private static final String BLANK_SPACE = " ";

    private String response;

    private HttpResponseStatusLine httpResponseStatusLine;
    private HttpResponseHeaders httpResponseHeaders;
    private HttpResponseBody httpResponseBody;

    public HttpResponse(String response) {
        this.response = response;
    }

    public HttpResponse(HttpResponseStatusLine httpResponseStatusLine, HttpResponseHeaders httpResponseHeaders, HttpResponseBody httpResponseBody) {
        this.httpResponseStatusLine = httpResponseStatusLine;
        this.httpResponseHeaders = httpResponseHeaders;
        this.httpResponseBody = httpResponseBody;
    }

    public static HttpResponse from(ResponseEntity responseEntity) throws IOException {
        String location = responseEntity.getLocation();
        HttpResponseBody responseBody = responseEntity.getResponseBody();
        HttpStatus httpStatus = responseEntity.getHttpStatus();

        if (responseBody == null) {
            responseBody = generateResponseBody(location);
        }

        if (httpStatus == HttpStatus.FOUND) {
            String formatResponse = String.join(
                    DELIMITER,
                    generateHttpStatus(httpStatus),
                    generateLocation(responseEntity.getLocation()),
                    generateCookie(responseEntity.getHttpCookie())
            );
            return new HttpResponse(formatResponse);
        }

        String formatResponse = String.join(
                DELIMITER,
                generateHttpStatus(responseEntity.getHttpStatus()),
                generateContentType(responseEntity.getContentType().getName()),
                generateContentLength(responseBody),
                BLANK_LINE,
                responseBody.getBody()
        );
        return new HttpResponse(formatResponse);
    }

    private static HttpResponseBody generateResponseBody(String location) throws IOException {
        URL resource = ClassLoader.getSystemClassLoader().getResource("static" + location);
        File file = new File(resource.getFile());
        return HttpResponseBody.from(new String(Files.readAllBytes(file.toPath())));
    }

    private static String generateHttpStatus(HttpStatus status) {
        return String.format("HTTP/1.1 %s %s ", status.getStatusCode(), status.name());
    }

    private static String generateLocation(String location) {
        if (location == null) {
            return "";
        }
        return String.format("Location: %s ", location);
    }

    private static String generateContentType(String requestURI) {
        if (requestURI.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    private static String generateContentLength(HttpResponseBody responseBody) {
        String body = responseBody.getBody();
        return String.format("Content-Length: %s ", body.getBytes().length);
    }

    private static String generateCookie(HttpCookie httpCookie) {
        if (httpCookie == null) {
            return "";
        }
        String jSessionId = httpCookie.findJSessionId();
        if (jSessionId == null) {
            return "";
        }
        return String.format("Set-Cookie: JSESSIONID=%s ", jSessionId);
    }
}
