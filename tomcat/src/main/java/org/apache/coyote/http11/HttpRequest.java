package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.parser.HttpFormatException;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final String url;
    private final Map<String, String> queryString;
    private final HttpVersion httpVersion;
    private final Map<String, String> headers;
    private final String body;

    // TODO: 공통 헤더 및 요청에 대한 필수 헤더 검증 추가
    public HttpRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = bufferedReader.readLine();
        String[] elements = line.split(" ");
        if (elements.length != 3) {
            throw new HttpFormatException();
        }
        this.httpMethod = HttpMethod.convertFrom(elements[0].trim());

        String[] urlAndQueryString = elements[1].trim().split("\\?");
        this.url = urlAndQueryString[0];

        Map<String, String> queryString = new HashMap<>();
        if (urlAndQueryString.length > 2) {
            throw new HttpFormatException();
        } else if (urlAndQueryString.length == 2) {
            String[] queryStrings = urlAndQueryString[1].split("&");
            for (String singleQueryString : queryStrings) {
                String[] keyValue = singleQueryString.split("=");
                if (keyValue.length != 2) {
                    throw new HttpFormatException();
                }
                queryString.put(keyValue[0], keyValue[1]);
            }
        }
        this.queryString = queryString;

        String[] httpVersion = elements[2].split("/");
        if (httpVersion.length != 2 && !httpVersion[0].equals("HTTP")) {
            throw new HttpFormatException();
        }
        this.httpVersion = HttpVersion.convertFrom(httpVersion[1]);

        Map<String, String> headers = new HashMap<>();
        while (!(line = bufferedReader.readLine()).equals("")) {
            String[] keyValue = line.split(": ");
            if (keyValue.length < 2) {
                throw new HttpFormatException();
            }
            headers.put(keyValue[0], line.substring(keyValue.length + ": ".length() + 1));
        }
        this.headers = headers;

        StringBuilder bodyBuilder = new StringBuilder();
        while (bufferedReader.ready()) {
            bodyBuilder.append(line);
        }

        this.body = bodyBuilder.toString();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
