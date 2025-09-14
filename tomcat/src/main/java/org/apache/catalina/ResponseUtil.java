package org.apache.catalina;

import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ResponseUtil {

    private static final String JSESSIONID = "JSESSIONID";

    public static HttpResponse buildRedirectResponse(String location, String httpVersion) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", location);
        return new HttpResponse(
                httpVersion,
                HttpStatus.FOUND.getCode(),
                HttpStatus.FOUND.getReasonPhrase(),
                headers,
                ""
        );
    }

    public static HttpResponse buildLoginSuccessResponse(String sessionId, String indexPage, String httpVersion) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Location", indexPage);
        headers.put("Set-Cookie", JSESSIONID + "=" + sessionId);
        return new HttpResponse(
                httpVersion,
                HttpStatus.FOUND.getCode(),
                HttpStatus.FOUND.getReasonPhrase(),
                headers,
                ""
        );
    }

    public static HttpResponse buildNotFoundResponse(String httpVersion) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");

        try (InputStream inputStream = ResponseUtil.class.getClassLoader()
                .getResourceAsStream("static/404.html")) {
            if (inputStream == null) {
                String responseBody = "<html><body><h1>404 Not Found</h1></body></html>";
                return new HttpResponse(
                        httpVersion,
                        HttpStatus.NOT_FOUND.getCode(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(),
                        headers,
                        responseBody
                );
            } else {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String responseBody = reader.lines().collect(Collectors.joining("\n"));
                    return new HttpResponse(
                            "HTTP/1.1",
                            HttpStatus.NOT_FOUND.getCode(),
                            HttpStatus.NOT_FOUND.getReasonPhrase(),
                            headers,
                            responseBody
                    );
                }
            }
        }
    }

    public static HttpResponse buildMethodNotAllowedResponse(String httpVersion) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");

        try (InputStream inputStream = ResponseUtil.class.getClassLoader()
                .getResourceAsStream("static/405.html")) {
            if (inputStream == null) {
                String responseBody = "<html><body><h1>405 Method Not Allowed</h1></body></html>";
                return new HttpResponse(
                        httpVersion,
                        HttpStatus.METHOD_NOT_ALLOWED.getCode(),
                        HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                        headers,
                        responseBody
                );
            } else {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                    String responseBody = reader.lines().collect(Collectors.joining("\n"));
                    return new HttpResponse(
                            "HTTP/1.1",
                            HttpStatus.METHOD_NOT_ALLOWED.getCode(),
                            HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                            headers,
                            responseBody
                    );
                }
            }
        }
    }
}