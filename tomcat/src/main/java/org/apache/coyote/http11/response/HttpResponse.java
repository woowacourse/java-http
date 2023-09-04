package org.apache.coyote.http11.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
        HttpStatus httpStatus = responseEntity.getHttpStatus();
        String status = httpStatus.getStatusCode() + " " + httpStatus.name();

        String location = responseEntity.getLocation();
        String responseBody = responseEntity.getResponseBody();

        if (responseBody == null) {
            URL resource = ClassLoader.getSystemClassLoader().getResource("static" + location);
            File file = new File(resource.getFile());
            responseBody = new String(Files.readAllBytes(file.toPath()));
        }

        String formatResponse = String.join(
                DELIMITER,
                generateHttpStatus(status),
                generateContentType(responseEntity.getRequestURI()),
                generateContentLength(responseBody),
                BLANK_LINE,
                responseBody
        );
        return new HttpResponse(formatResponse);
    }

    private static String generateHttpStatus(String status) {
        return "HTTP/1.1 " + status + " ";
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
}
