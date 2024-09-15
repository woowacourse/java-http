package org.apache.catalina;

import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.request.RequestBody;
import org.apache.catalina.request.RequestHeader;
import org.apache.catalina.request.StartLine;

import java.util.ArrayList;
import java.util.List;

public class HttpRequestFixture {

    private static final List<String> headers = new ArrayList<>(List.of(
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Accept: */*"
    ));

    public static HttpRequest getHttpGetRequest(String uri) {
        StartLine startLine = StartLine.parse(String.format("GET %s HTTP/1.1", uri));
        RequestHeader requestHeader = RequestHeader.parse(headers);
        RequestBody requestBody = RequestBody.empty();
        return new HttpRequest(startLine, requestHeader, requestBody);
    }

    public static HttpRequest getHttpGetRequestWithLoginUser(String uri) {
        StartLine startLine = StartLine.parse(String.format("GET %s HTTP/1.1", uri));
        headers.add("Cookie: JSESSIONID=1");
        RequestHeader requestHeader = RequestHeader.parse(headers);
        RequestBody requestBody = RequestBody.empty();
        return new HttpRequest(startLine, requestHeader, requestBody);
    }

    public static HttpRequest getHttpPostRequest(String uri, String rawBody) {
        StartLine startLine = StartLine.parse(String.format("POST %s HTTP/1.1", uri));
        RequestHeader requestHeader = RequestHeader.parse(headers);
        RequestBody requestBody = RequestBody.parse(rawBody);
        return new HttpRequest(startLine, requestHeader, requestBody);
    }
}
