package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.util.HttpStartLineParser;
import org.apache.coyote.http11.util.StringUtils;

public class HttpRequest {

    private static final String HEADER_DELIMITER = ": ";
    private static final String EMPTY = "EMPTY";

    private HttpMethod httpMethod;
    private String httpUrl;
    private Map<String, String> queryParams;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> requestBody = new HashMap<>();

    public HttpRequest(BufferedReader bufferedReader) throws IOException {
        String startLine = bufferedReader.readLine();
        HttpStartLineParser httpStartLineParser = new HttpStartLineParser(startLine);
        this.httpMethod = httpStartLineParser.getHttpMethod();
        this.httpUrl = httpStartLineParser.getHttpUrl();
        this.queryParams = httpStartLineParser.getQueryParams();
        parseHeaders(bufferedReader);

        if (HttpMethod.POST.equals(httpMethod)) {
            parseRequestBody(bufferedReader);
        }
    }

    private void parseHeaders(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        while (line != null && !line.isBlank()) {
            String[] header = line.split(HEADER_DELIMITER);
            headers.put(header[0], header[1]);
            line = bufferedReader.readLine();
        }
    }

    private void parseRequestBody(BufferedReader bufferedReader) throws IOException {
        String contentLength = headers.getOrDefault("Content-Length", "0");
        int length = Integer.parseInt(contentLength);
        char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        requestBody = StringUtils.parseKeyAndValues(new String(buffer));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getRequestBody() {
        return requestBody;
    }
}
