package org.apache.coyote.request;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpCookie;

public class HttpRequest {
    public static final String CRLF = "\r\n";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private Session session;

    public static HttpRequest parse(String requestHead) {
        String[] requestLines = requestHead.split(CRLF);

        RequestLine requestLine = RequestLine.parse(requestLines[0]);

        Map<String, String> headers = parseHeader(requestLines);
        Map<String, String> parameters = new HashMap<>();

        return new HttpRequest(requestLine, headers, parameters, null);
    }

    private static Map<String, String> parseHeader(String[] requestLines) {
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < requestLines.length; i++) {
            String header = requestLines[i];

            String[] keyAndValue = header.split(":", 2);
            if (keyAndValue.length != 2 || keyAndValue[0].isBlank()) {
                throw new IllegalArgumentException("헤더 형식이 올바르지 않습니다.");
            }
            headers.put(keyAndValue[0], keyAndValue[1].trim());
        }

        return headers;
    }

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, Map<String, String> parameters,
                        Session session) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.parameters = parameters;
        this.session = session;
    }

    public void parseBody(String requestBody) {
        String[] queryPairs = requestBody.split("&");
        for (String queryPair : queryPairs) {
            String[] keyAndValue = queryPair.split("=", 2);

            if (keyAndValue.length == 2) {
                String key = URLDecoder.decode(keyAndValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8);

                parameters.put(key, value);
            }
        }
    }

    public Integer getContentLength() {
        String contentLength = headers.get("Content-Length");

        if (contentLength == null) {
            return null;
        }
        return Integer.parseInt(contentLength);
    }

    public HttpCookie getCookie() {
        String cookieLine = headers.getOrDefault("Cookie", null);
        return HttpCookie.parse(cookieLine);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getParameters(String name) {
        return parameters.get(name);
    }

    public String getRequestTarget() {
        return requestLine.getRequestTarget();
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
