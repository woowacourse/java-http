package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestHandler {

    public HttpRequest handleRequest(InputStream inputStream) throws IOException {
        byte[] allBytes = inputStream.readAllBytes();

        int headerBodySeparatorIndex = findHeaderBodySeparator(allBytes);

        if (headerBodySeparatorIndex == -1) {
            headerBodySeparatorIndex = allBytes.length;
        }

        byte[] headerBytes = Arrays.copyOfRange(allBytes, 0, headerBodySeparatorIndex);
        byte[] bodyBytes = Arrays.copyOfRange(allBytes, headerBodySeparatorIndex, allBytes.length);

        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(headerBytes)));

        HttpStartLine startLine = readRequestLine(reader);
        HttpHeader header = readHeader(reader);
        HttpRequestBody body = new HttpRequestBody(bodyBytes);
        HttpQueryParameter queryParameter = readQueryString(startLine.getUri());

        return new HttpRequest(startLine, header, body,queryParameter);
    }

    private int findHeaderBodySeparator(final byte[] data) {
        for (int i = 0; i < data.length - 3; i++) {
            if (data[i] == 13 && data[i + 1] == 10 && data[i + 2] == 13 && data[i + 3] == 10) {
                return i + 4;
            }
        }
        return -1;
    }

    private HttpStartLine readRequestLine(BufferedReader br) throws IOException {
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException();
        }

        String[] requestLineSegments = requestLine.split(" ");
        String methodSegment = requestLineSegments[0];
        HttpMethod method = HttpMethod.getMethod(methodSegment);

        String uriSegment = requestLineSegments[1];
        HttpUri httpUri = new HttpUri(uriSegment);

        String versionSegment = requestLineSegments[2];
        HttpProtocol protocol = HttpProtocol.getHttpProtocol(versionSegment);

        return new HttpStartLine(method, httpUri, protocol);
    }

    private HttpHeader readHeader(BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;
        while(!"".equals((line = br.readLine()))) {
            if (line == null) {
                throw new IllegalArgumentException();
            }
            int index = line.indexOf(":");
            String fieldName = line.substring(0, index);
            String value = line.substring(index + 1);
            headers.put(fieldName, value);
        }
        return new HttpHeader(headers);
    }

    private HttpQueryParameter readQueryString(HttpUri uri) {
        Map<String, String> queryParameters = new HashMap<>();

        String queryString = uri.getQueryString();
        if (queryString.isBlank()) {
            return new HttpQueryParameter(Map.of());
        }

        String[] parameters = queryString.split("&");
        for (String parameter : parameters) {
            int index = parameter.indexOf("=");
            String key = parameter.substring(0, index);
            String value = parameter.substring(index + 1);
            queryParameters.put(key, value);
        }
        return new HttpQueryParameter(queryParameters);
    }
}
