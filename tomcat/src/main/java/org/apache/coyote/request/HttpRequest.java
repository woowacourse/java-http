package org.apache.coyote.request;


import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpCookie;

public class HttpRequest {
    public static final String CRLF = "\r\n";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;

    public static HttpRequest parse(String requestHead, String body) {
        String[] requestLines = requestHead.split(CRLF);

        RequestLine requestLine = RequestLine.parse(requestLines[0]);

        Map<String, String> headers = parseHeader(requestLines);
        Map<String, String> parameters = new HashMap<>();
        if(requestLine.getHttpMethod() == HttpMethod.POST){
            parameters = parseFormParameters(body);
        }

        return new HttpRequest(requestLine, headers, parameters);
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

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, Map<String, String> parameters) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.parameters = parameters;
    }

    private static Map<String, String> parseFormParameters(String queryString) {
        Map<String, String> encodedParameters = new HashMap<>();

        String[] queryPairs = queryString.split("&");
        for (String queryPair : queryPairs) {
            String[] keyAndValue = queryPair.split("=", 2);

            if (keyAndValue.length == 2) {
                String key = URLDecoder.decode(keyAndValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8);

                encodedParameters.put(key, value);
            }
        }
        return encodedParameters;
    }

    public HttpCookie getCookie(){
        String cookieLine = headers.getOrDefault("Cookie", null);
        return HttpCookie.parse(cookieLine);
    }

    public HttpMethod getHttpMethod() {
        return requestLine.getHttpMethod();
    }

    public String getParameters(String name){
        return parameters.get(name);
    }

    public String getRequestTarget() {
        return requestLine.getRequestTarget();
    }
}
