package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {
    private final Map<String, String> requestHeader;
    private final String requestBody;
    private final HttpCookie httpCookie;

    private HttpRequest(Map<String, String> requestHeader, String requestBody, HttpCookie httpCookie) {
        this.requestHeader = requestHeader;
        this.requestBody = requestBody;
        this.httpCookie = httpCookie;
    }

    public static HttpRequest of(BufferedReader reader) throws IOException {
        Map<String, String> header = parseHeader(reader);
        String requestBody = parseBody(reader, header);
        HttpCookie httpCookie = HttpCookie.of(header.get("Cookie"));

        return new HttpRequest(header, requestBody, httpCookie);
    }

    private static Map<String, String> parseHeader(BufferedReader reader) throws IOException {
        Map<String, String> header = new HashMap<>();

        String[] request = reader.readLine().split(" ");
        header.put("HTTP Method", request[0]);
        header.put("request URI", request[1]);
        header.put("HTTP Version", request[2]);

        String line = reader.readLine();
        while (!line.isEmpty()) {
            request = line.split(": ");
            header.put(request[0], request[1]);
            line = reader.readLine();
        }
        return header;
    }

    private static String parseBody(BufferedReader reader, Map<String, String> header) throws IOException {
        int contentLength = Integer.parseInt(header.getOrDefault("Content-Length", "0"));

        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public HttpMethod getHttpMethod() {
        return HttpMethod.valueOf(requestHeader.get("HTTP Method"));
    }

    public String getPath() {
        String uri = requestHeader.get("request URI");
        int index = uri.indexOf("?");

        if (index != -1) {
            return uri.substring(0, index);
        }
        return uri;
    }

    public Map<String, String> getRequestBody() {
        return Arrays.stream(requestBody.split("&"))
            .map(body -> body.split("="))
            .collect(Collectors.toMap(query -> query[0], query -> query[1]));
    }

    public String getCookie(String key) {
        return httpCookie.getOrDefault(key);
    }

    public boolean isSamePath(String... paths) {
        String realPath = getPath();

        return Arrays.asList(paths).contains(realPath);
    }
}
