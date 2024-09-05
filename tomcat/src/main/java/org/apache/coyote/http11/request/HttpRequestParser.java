package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    private static final String DELIMITER_REQUEST_LINE = " ";

    private static final String DELIMITER_HEADER = ": ";

    private static final String EMPTY_LINE = "";

    public HttpRequest parseRequest(BufferedReader bufferedReader) throws IOException {
        String [] requestLine = bufferedReader.readLine().split(DELIMITER_REQUEST_LINE);
        HttpMethod httpMethod = parseHttpMethod(requestLine);
        HttpRequestPath httpRequestPath = parseHttpRequestPath(requestLine);
        HttpHeaders httpHeaders = parseHttpHeaders(bufferedReader);

        return new HttpRequest(httpMethod, httpRequestPath, httpHeaders);
    }

    HttpMethod parseHttpMethod(String[] requestLine) {
        return HttpMethod.valueOf(requestLine[0]);
    }

    HttpRequestPath parseHttpRequestPath(String[] requestLine) {
        return new HttpRequestPath(requestLine[1]);
    }

    HttpHeaders parseHttpHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine = bufferedReader.readLine();
        while(!EMPTY_LINE.equals(headerLine) && validateNullLine(headerLine)) {
            String[] headerInfo = headerLine.split(DELIMITER_HEADER);
            String key = headerInfo[0];
            String value = headerInfo[1];
            headers.put(key, value);
            headerLine = bufferedReader.readLine();
        }
        return new HttpHeaders(headers);
    }

    boolean validateNullLine(String line) {
        return line != null;
    }
}
