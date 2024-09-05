package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class HttpRequestParser {

    public HttpRequest extractRequest(InputStream inputStream) throws IOException {
        final String requestStrings = extractStrings(inputStream);
        int bodyIndex = requestStrings.indexOf("\r\n\r\n");
        String requestBody = "";
        if (bodyIndex != -1) {
            requestBody = requestStrings.substring(bodyIndex + 4);
        }
        String[] requestLine = parseRequestLine(requestStrings);
        String httpMethod = requestLine[0];
        String uri = requestLine[1];
        String protocol = requestLine[2];
        int resourceIndex = uri.indexOf("/");
        String path = uri.substring(resourceIndex + 1);
        String headers = parseHeaders(requestStrings.split("\r\n"));
        String[] pairs = requestBody.split("&");

        return new HttpRequest(httpMethod, uri, path, pairs, protocol, headers, requestBody);
    }

    public String extractStrings(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder str = new StringBuilder();
        int ascii;
        while (br.ready() && (ascii = br.read()) != -1) {
            str.append((char) ascii);
        }

        return str.toString();
    }

    private String[] parseRequestLine(String httpRequest) {
        String[] lines = httpRequest.split("\r\n");
        return lines[0].split(" ");
    }

    private String parseHeaders(String[] requestStrings) {
        return Arrays.toString(Arrays.copyOfRange(requestStrings, 1, requestStrings.length));
    }
}
