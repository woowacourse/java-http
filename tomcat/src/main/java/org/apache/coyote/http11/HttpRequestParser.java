package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringJoiner;

public class HttpRequestParser {

    public HttpRequest extractRequest(InputStream inputStream) throws IOException {
        final String requestStrings = extractStrings(inputStream);
        String[] requestLine = parseRequestLine(requestStrings);
        String httpMethod = requestLine[0];
        String uri = requestLine[1];
        String path = "";
        String queryParams;
        String protocol = requestLine[2];
        String headers = parseHeaders(requestStrings.split("\r\n"));
        String[] pairs = new String[0];
        int resourceIndex = uri.indexOf("/");
        path = uri.substring(resourceIndex + 1);
        if (uri.contains("?")) {
            int queryIndex = uri.indexOf("?");
            queryParams = uri.substring(queryIndex + 1);
            pairs = queryParams.split("&");
            path = uri.substring(resourceIndex + 1, queryIndex);
        }
        return new HttpRequest(httpMethod, uri, path, pairs, protocol, headers);
    }

    public String extractStrings(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        final StringJoiner stringJoiner = new StringJoiner("\r\n");

        String str = br.readLine();
        while (br.ready()) {
            stringJoiner.add(str);
            str = br.readLine();
        }
        stringJoiner.add("");

        return stringJoiner.toString();
    }

    private String[] parseRequestLine(String httpRequest) {
        String[] lines = httpRequest.split("\r\n");
        return lines[0].split(" ");
    }

    private String parseHeaders(String[] requestStrings) {
        return Arrays.toString(Arrays.copyOfRange(requestStrings, 1, requestStrings.length));
    }
}
