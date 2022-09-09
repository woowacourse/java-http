package org.apache.coyote.http11.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequestParser {

    private final HttpMethod httpMethod;
    private final HttpRequestURI requestURI;
    private final HttpHeader httpRequestHeader;
    private final HttpRequestBody requestBody;
    private final HttpCookie httpCookie;

    private HttpRequestParser(HttpMethod httpMethod, HttpRequestURI requestURI, HttpHeader httpRequestHeader,
        HttpCookie cookie, HttpRequestBody requestBody) {
        this.httpMethod = httpMethod;
        this.requestURI = requestURI;
        this.httpRequestHeader = httpRequestHeader;
        this.httpCookie = cookie;
        this.requestBody = requestBody;
    }

    public static HttpRequestParser from(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = bufferedReader.readLine();
        StringTokenizer requestTokenizer = new StringTokenizer(requestLine);
        HttpMethod httpMethod = HttpMethod.of(requestTokenizer.nextToken());
        HttpRequestURI requestURI = HttpRequestURI.from(requestTokenizer.nextToken());

        HttpHeader httpRequestHeaders = createRequestHeaders(bufferedReader);
        HttpCookie httpCookie = createCookie(httpRequestHeaders);
        HttpRequestBody httpRequestBody = createRequestBody(bufferedReader, httpRequestHeaders);

        return new HttpRequestParser(httpMethod, requestURI, httpRequestHeaders, httpCookie, httpRequestBody);
    }

    private static HttpHeader createRequestHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> httpRequestHeader = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            StringTokenizer headerTokenizer =
                new StringTokenizer(line.replaceAll(" ", ""), ":");
            httpRequestHeader.put(headerTokenizer.nextToken(), headerTokenizer.nextToken());
            line = bufferedReader.readLine();
        }
        return HttpHeader.from(httpRequestHeader);
    }

    private static HttpCookie createCookie(HttpHeader httpRequestHeader) {
        Map<String, String> cookies = new HashMap<>();
        String cookie = httpRequestHeader.getAttributeOrDefault("Cookie", "")
            .replaceAll(" ", "");
        StringTokenizer cookieTokenizer = new StringTokenizer(cookie, ";");
        while (cookieTokenizer.hasMoreTokens()) {
            String attribute = cookieTokenizer.nextToken();
            StringTokenizer attributeTokenizer = new StringTokenizer(attribute, "=");
            cookies.put(attributeTokenizer.nextToken(), attributeTokenizer.nextToken());
        }
        return HttpCookie.from(cookies);
    }

    private static HttpRequestBody createRequestBody(BufferedReader bufferedReader, HttpHeader httpRequestHeader) throws
        IOException {
        Map<String, String> bodyParams = new HashMap<>();
        int contentLength = Integer.parseInt(httpRequestHeader.getAttributeOrDefault("Content-Length", "0"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        StringTokenizer bodyTokenizer = new StringTokenizer(new String(buffer).replaceAll("&", " "));
        while (bodyTokenizer.hasMoreTokens()) {
            String attribute = bodyTokenizer.nextToken();
            StringTokenizer attributeTokenizer = new StringTokenizer(attribute, "=");
            bodyParams.put(attributeTokenizer.nextToken(), attributeTokenizer.nextToken());
        }
        return HttpRequestBody.from(bodyParams);
    }

    public HttpRequest toHttpRequest() {
        return new HttpRequest(httpMethod, requestURI, httpRequestHeader, requestBody, httpCookie);
    }
}
