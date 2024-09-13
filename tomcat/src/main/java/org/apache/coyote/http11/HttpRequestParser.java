package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import org.apache.coyote.exception.UnexpectedHeaderException;
import org.apache.coyote.util.Symbol;

public class HttpRequestParser {

    public static String getFilePathFromUri(String path) {
        if (path.isEmpty() || path.equals(Symbol.URL_DELIMITER)) {
            return "index.html";
        }
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    public HttpRequest extractRequest(InputStream inputStream) throws IOException {
        final String requestStrings = extractStrings(inputStream);
        int bodyIndex = requestStrings.indexOf(Symbol.BODY_DELIMITER);
        String requestBody = Symbol.EMPTY;
        if (bodyIndex != -1) {
            requestBody = requestStrings.substring(bodyIndex + 4);
        }
        String[] requestLine = parseRequestLine(requestStrings);
        if (requestLine.length < 3) {
            throw new UnexpectedHeaderException(requestStrings);
        }
        String httpMethod = requestLine[0];
        String uri = requestLine[1];
        String protocol = requestLine[2];
        int resourceIndex = uri.indexOf(Symbol.URL_DELIMITER);
        String path = uri.substring(resourceIndex + 1);
        String[] headers = parseHeaders(requestStrings);
        String[] pairs = requestBody.split(Symbol.QUERY_STRING_DELIMITER);
        return new HttpRequest(httpMethod, uri, path, pairs, protocol, headers, requestBody);
    }

    private String extractStrings(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        StringBuilder str = new StringBuilder();
        int ascii;
        while (br.ready() && (ascii = br.read()) != -1) {
            str.append((char) ascii);
        }

        return str.toString();
    }

    private String[] parseRequestLine(String httpRequest) {
        String[] lines = httpRequest.split(Symbol.CRLF);
        for (String line : lines) {
            if (line.contains("HTTP/1.1")) {
                return line.split(Symbol.SPACE);
            }
        }
        return lines[0].split(Symbol.SPACE);
    }

    private String[] parseHeaders(String requestStrings) {
        String[] headers = requestStrings.split(Symbol.BODY_DELIMITER)[0].split(Symbol.CRLF);
        return Arrays.copyOfRange(headers, 1, headers.length);
    }
}
