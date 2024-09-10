package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.commons.lang3.StringUtils;

public class HttpReader {

    private final HttpRequest httpRequest;

    public HttpReader(InputStream inputStream) throws IOException {
        this.httpRequest = getRequest(inputStream);
    }

    private HttpRequest getRequest(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String firstLine = bufferedReader.readLine();

        RequestLine requestLine = getRequestLine(firstLine);
        System.out.println(requestLine.getRequestUrl());
        HttpHeader headers = getHeaders(bufferedReader);

        return new HttpRequest(requestLine, headers);
    }

    private RequestLine getRequestLine(String firstLine) {
        StringTokenizer stringTokenizer = new StringTokenizer(firstLine);

        String method = stringTokenizer.nextToken();
        String requestUrl = stringTokenizer.nextToken();
        String protocol = stringTokenizer.nextToken();
        return new RequestLine(method, requestUrl, protocol);
    }

    private HttpHeader getHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while ((line = bufferedReader.readLine()) != null && !StringUtils.isEmpty(line)) {
            String[] split = line.split(": ");
            headers.put(split[0], split[1]);
        }
        return new HttpHeader(headers);
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }
}
