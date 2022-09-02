package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpRequestParser {

    private final HttpMethod httpMethod;
    private final HttpURI uri;
    private final Map<String, String> httpRequestHeaders;
    private final Map<String, String> bodyParams;
    private final HttpCookie httpCookie;

    private HttpRequestParser(HttpMethod httpMethod, String uri, Map<String, String> httpRequestHeaders,
        Map<String, String> cookies, Map<String, String> bodyParams) {
        this.httpMethod = httpMethod;
        this.uri = HttpURI.from(uri);
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpCookie = HttpCookie.of(cookies);
        this.bodyParams = bodyParams;
    }

    public static HttpRequestParser from(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String requestLine = bufferedReader.readLine();
        StringTokenizer requestTokenizer = new StringTokenizer(requestLine);
        HttpMethod httpMethod = HttpMethod.of(requestTokenizer.nextToken());
        String uri = requestTokenizer.nextToken();

        Map<String, String> httpRequestHeaders = new HashMap<>();
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            StringTokenizer headerTokenizer =
                new StringTokenizer(line.replaceAll(" ", "").replaceAll(":", " "));
            httpRequestHeaders.put(headerTokenizer.nextToken(), headerTokenizer.nextToken());
            line = bufferedReader.readLine();
        }

        Map<String, String> cookies = new HashMap<>();
        String cookie = httpRequestHeaders.getOrDefault("Cookie", "")
            .replaceAll(" ", "")
            .replaceAll(";", " ");
        StringTokenizer cookieTokenizer = new StringTokenizer(cookie);
        while (cookieTokenizer.hasMoreTokens()) {
            String attribute = cookieTokenizer.nextToken().replaceAll("=", " ");
            StringTokenizer attributeTokenizer = new StringTokenizer(attribute);
            cookies.put(attributeTokenizer.nextToken(), attributeTokenizer.nextToken());
        }

        Map<String, String> bodyParams = new HashMap<>();
        int contentLength = Integer.parseInt(httpRequestHeaders.getOrDefault("Content-Length", "0"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        StringTokenizer bodyTokenizer = new StringTokenizer(new String(buffer).replaceAll("&", " "));
        while (bodyTokenizer.hasMoreTokens()) {
            String attribute = bodyTokenizer.nextToken().replaceAll("=", " ");
            StringTokenizer attributeTokenizer = new StringTokenizer(attribute);
            bodyParams.put(attributeTokenizer.nextToken(), attributeTokenizer.nextToken());
        }

        bufferedReader.close();
        return new HttpRequestParser(httpMethod, uri, httpRequestHeaders, cookies, bodyParams);
    }



    public HttpRequest toHttpRequest() {
        return new HttpRequest(httpMethod, uri, httpRequestHeaders, bodyParams, httpCookie);
    }
}
