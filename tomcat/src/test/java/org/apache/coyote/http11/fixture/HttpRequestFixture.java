package org.apache.coyote.http11.fixture;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.component.HttpCookie;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.MediaType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.request.RequestBody;

public class HttpRequestFixture {

    private static final String CRLF = "\r\n";

    private HttpRequestFixture() {
    }

    public static String getGetRequestMessage(String path) {
        return String.join(CRLF,
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
    }

    public static String getGetRequestWithSessionMessage(String path, String sessionId) {
        return String.join(CRLF,
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                HttpHeaders.COOKIE + ": " + HttpCookie.JSESSIONID + "=" + sessionId,
                "",
                ""
        );
    }

    public static String getPostRequestMessage(String path, Map<String, String> requestBody) {
        String body = requestBody.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        return String.join(
                CRLF,
                "POST " + path + " HTTP/1.1 ",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7 ",
                "Accept-Encoding: gzip, deflate, br, zstd ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Cookie: Idea-5989f5cb=0d4ee1f0-b123-481e-828f-c8a23f5ae25a; JSESSIONID=36526710-c7f8-4659-aaca-4cf4542be1c8 ",
                "Host: localhost:8080",
                "",
                body
        );
    }

    public static HttpRequest getGetRequest(String path) {
        HttpRequestLine requestLine = HttpRequestLine.from("GET " + path + " HTTP/1.1");
        HttpRequestHeader requestHeader = HttpRequestHeader.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        ));
        RequestBody body = RequestBody.from("", null);
        return new HttpRequest(requestLine, requestHeader, body);
    }

    public static HttpRequest getGetRequestWithSession(String path, String sessionId) {
        HttpRequestLine requestLine = HttpRequestLine.from("GET " + path + " HTTP/1.1");
        HttpRequestHeader requestHeader = HttpRequestHeader.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                HttpHeaders.COOKIE + ": " + HttpCookie.JSESSIONID + "=" + sessionId
        ));
        RequestBody body = RequestBody.from("", null);
        return new HttpRequest(requestLine, requestHeader, body);
    }

    public static HttpRequest getPostRequest(String path, Map<String, String> requestBody) {
        String body = requestBody.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        HttpRequestLine requestLine = HttpRequestLine.from("POST " + path + " HTTP/1.1");
        MediaType mediaType = MediaType.APPLICATION_FORM_URLENCODED;
        HttpRequestHeader requestHeader = HttpRequestHeader.from(List.of(
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7 ",
                "Accept-Encoding: gzip, deflate, br, zstd ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: " + mediaType.getValue() + " ",
                "Cookie: Idea-5989f5cb=0d4ee1f0-b123-481e-828f-c8a23f5ae25a; JSESSIONID=36526710-c7f8-4659-aaca-4cf4542be1c8 ",
                "Host: localhost:8080"
        ));
        return new HttpRequest(requestLine, requestHeader, RequestBody.from(body, mediaType.getValue()));
    }
}
