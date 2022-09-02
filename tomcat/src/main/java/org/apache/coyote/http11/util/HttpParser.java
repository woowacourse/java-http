package org.apache.coyote.http11.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;

public class HttpParser {

    private HttpMethod httpMethod;
    private String httpUrl;
    private Map<String, String> queryParam;

    public HttpParser(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String[] startLine = bufferedReader.readLine().split(" ");
            httpMethod = HttpMethod.from(startLine[0]);
            httpUrl = startLine[1];
            queryParam = new LinkedHashMap<>();
            extractQueryParams();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void extractQueryParams() {
        if (httpUrl.contains("?")) {
            String queryString = httpUrl.split("\\?")[1];
            String[] keyAndValues = queryString.split("&");
            extractKeyAndValues(keyAndValues);
        }
    }

    private void extractKeyAndValues(String[] keyAndValues) {
        for (String keyAndValue : keyAndValues) {
            String[] keyValue = keyAndValue.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            queryParam.put(key, value);
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public Map<String, String> getQueryParam() {
        return queryParam;
    }
}
