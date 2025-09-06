package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final Method method;
    private final String path;
    private final HttpVersion httpVersion;
    private final ContentType contentType;
    private final Map<String, String> queryParameter;

    private HttpRequest(Method method,
                        String path,
                        HttpVersion httpVersion,
                        ContentType contentType,
                        Map<String, String> queryParameter) {
        this.method = method;
        this.path = path;
        this.httpVersion = httpVersion;
        this.contentType = contentType;
        this.queryParameter = queryParameter;
    }

    public static HttpRequest of(List<String> rawHttpRequest) {
        String[] firstLine = rawHttpRequest.get(0).split(" ");
        Method method = Method.fromHeaderValue(firstLine[0].trim());
        String resource = firstLine[1].trim();
        Map<String, String> queryParameter = new HashMap<>();
        if (resource.contains("?")) {
            getQueryParameter(resource, queryParameter);
            resource = resource.substring(0, resource.indexOf("?"));
        }
        HttpVersion httpVersion = HttpVersion.fromHeaderValue(firstLine[2].trim());
        ContentType contentType = getHttpContentType(rawHttpRequest);
        return new HttpRequest(method, resource, httpVersion, contentType, queryParameter);
    }

    private static void getQueryParameter(String resource, Map<String, String> queryParameter) {
        String queryString = resource.substring(resource.indexOf("?") + 1);
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                queryParameter.put(keyValue[0], keyValue[1]);
            }
        }
    }

    private static ContentType getHttpContentType(List<String> rawHttpRequest) {
        for (String line : rawHttpRequest) {
            if (line.startsWith("Accept:")) {
                String acceptValue = line.substring(("Accept: ").length()).trim();

                int semicolonIndex = acceptValue.indexOf(',');
                if (semicolonIndex != -1) {
                    acceptValue = acceptValue.substring(0, semicolonIndex).trim();
                }

                return ContentType.fromHeaderValue(acceptValue.trim());
            }
        }
        return ContentType.ALL;
    }

    public String getQueryParameterValue(String key) {
        String value = queryParameter.get(key);
        if (value == null || value.isBlank()) {
            return "";
        }
        return value;
    }

    public Method getHttpMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public ContentType getHttpContentType() {
        return contentType;
    }

    public Map<String, String> getQueryParameter() {
        return queryParameter;
    }

    @Override
    public String toString() {
        return "RequestData{" +
                "httpMethod=" + method +
                ", resource='" + path + '\'' +
                ", httpVersion=" + httpVersion +
                ", httpContentType=" + contentType +
                ", queryParameter=" + queryParameter +
                '}';
    }
}
