package org.apache.coyote.http11.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.http11.session.HttpCookie;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

@Getter
@RequiredArgsConstructor
public class HttpResponse {

    private static final String DELIMITER = "\r\n";
    private static final String BLANK_LINE = "";

    private final String response;

    public static HttpResponse from(ResponseEntity responseEntity) throws IOException {
        String location = responseEntity.getLocation();
        String responseBody = responseEntity.getResponseBody();
        HttpStatus httpStatus = responseEntity.getHttpStatus();

        if (responseBody == null) {
            responseBody = findResourceFromLocation(location);
        }

        if (httpStatus == HttpStatus.FOUND) {
            String formatResponse = String.join(
                    DELIMITER,
                    generateHttpStatus(httpStatus),
                    generateLocation(responseEntity),
                    generateCookie(responseEntity)
            );
            return new HttpResponse(formatResponse);
        }

        String formatResponse = String.join(
                DELIMITER,
                generateHttpStatus(responseEntity.getHttpStatus()),
                generateContentType(responseEntity.getRequestURI()),
                generateContentLength(responseBody),
                BLANK_LINE,
                responseBody
        );
        return new HttpResponse(formatResponse);
    }

    private static String findResourceFromLocation(String location) throws IOException {
        String responseBody;
        URL resource = ClassLoader.getSystemClassLoader().getResource("static" + location);
        File file = new File(resource.getFile());
        responseBody = new String(Files.readAllBytes(file.toPath()));
        return responseBody;
    }

    private static String generateHttpStatus(HttpStatus status) {
        return "HTTP/1.1 " + status.getStatusCode() + " " + status.name() + " ";
    }

    private static String generateLocation(ResponseEntity responseEntity) {
        String location = responseEntity.getLocation();
        if (location == null) {
            return "";
        }
        return "Location: " + location + " ";
    }

    private static String generateContentType(String requestURI) {
        if (requestURI.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }
        return "Content-Type: text/html;charset=utf-8 ";
    }

    private static String generateContentLength(String responseBody) {
        return "Content-Length: " + responseBody.getBytes().length + " ";
    }

    private static String generateCookie(ResponseEntity responseEntity) {
        HttpCookie httpCookie = responseEntity.getHttpCookie();
        if (httpCookie == null) {
            return "";
        }
        String jSessionId = httpCookie.findJSessionId();
        if (jSessionId == null) {
            return "";
        }
        return "Set-Cookie: JSESSIONID=" + jSessionId + " ";
    }
}
