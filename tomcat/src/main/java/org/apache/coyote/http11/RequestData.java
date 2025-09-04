package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestData {

    private final HttpMethod httpMethod;
    private final String resource;
    private final HttpVersion httpVersion;
    private final HttpContentType httpContentType;
    private final Map<String, String> queryParameter;

    private RequestData(HttpMethod httpMethod,
                        String resource,
                        HttpVersion httpVersion,
                        HttpContentType httpContentType,
                       Map<String, String> queryParameter) {
        this.httpMethod = httpMethod;
        this.resource = resource;
        this.httpVersion = httpVersion;
        this.httpContentType = httpContentType;
        this.queryParameter = queryParameter;
    }

    public static RequestData of(List<String> rawHttpRequest) {
        String[] firstLine = rawHttpRequest.get(0).split(" ");
        HttpMethod httpMethod = HttpMethod.getHttpMethod(firstLine[0].trim());
        String resource = firstLine[1].trim();
        Map<String, String> queryParameter = new HashMap<>();
        if(resource.contains("?")) {
            getQueryParameter(resource, queryParameter);
            resource = resource.substring(0, resource.indexOf("?"));
        }
        HttpVersion httpVersion = HttpVersion.getHttpVersion(firstLine[2].trim());
        HttpContentType httpContentType = getHttpContentType(rawHttpRequest);
        return new RequestData(httpMethod, resource, httpVersion, httpContentType, queryParameter);
    }

    private static void getQueryParameter(String resource, Map<String, String> queryParameter) {
        String queryString = resource.substring(resource.indexOf("?") + 1);
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=");
            if(keyValue.length == 2) {
                queryParameter.put(keyValue[0], keyValue[1]);
            }
        }
    }

    private static HttpContentType getHttpContentType(List<String> rawHttpRequest) {
        for (String line : rawHttpRequest) {
            if (line.startsWith("Accept:")) {
                String acceptValue = line.substring(("Accept: ").length()).trim();

                int semicolonIndex = acceptValue.indexOf(',');
                if (semicolonIndex != -1) {
                    acceptValue = acceptValue.substring(0, semicolonIndex).trim();
                }

                return HttpContentType.getHttpContentType(acceptValue.trim());
            }
        }
        return HttpContentType.ALL;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getResource() {
        return resource;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpContentType getHttpContentType() {
        return httpContentType;
    }

    public Map<String, String> getQueryParameter() {
        return queryParameter;
    }

    @Override
    public String toString() {
        return "RequestData{" +
                "httpMethod=" + httpMethod +
                ", resource='" + resource + '\'' +
                ", httpVersion=" + httpVersion +
                ", httpContentType=" + httpContentType +
                ", queryParameter=" + queryParameter +
                '}';
    }
}
