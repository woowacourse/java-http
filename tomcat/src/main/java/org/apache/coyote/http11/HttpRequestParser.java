package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import org.apache.coyote.exception.UnexpectedHeaderException;
import org.apache.coyote.util.Symbol;

public class HttpRequestParser {

    public HttpRequest extractRequest(InputStream inputStream) throws IOException {
        final String requestStrings = extractStrings(inputStream);
        int bodyIndex = requestStrings.indexOf(Symbol.BODY_DELIMITER);
        String requestBody = Symbol.EMPTY;
        if (bodyIndex != -1) {
            requestBody = requestStrings.substring(bodyIndex + 4);
        }
        String[] requestLine = parseRequestLine(requestStrings);
        if (requestLine.length == 3) {
            String httpMethod = requestLine[0];
            String uri = requestLine[1];
            String protocol = requestLine[2];
            int resourceIndex = uri.indexOf(Symbol.CRLF);
            String path = uri.substring(resourceIndex + 1);
            String[] headers = parseHeaders(requestStrings);
            String[] pairs = requestBody.split(Symbol.QUERY_STRING_DELIMITER);

            return new HttpRequest(httpMethod, uri, path, pairs, protocol, headers, requestBody);
        }
        throw new UnexpectedHeaderException(String.join(Symbol.SPACE, requestLine));
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
        return lines[0].split(Symbol.SPACE);
    }

    private String[] parseHeaders(String requestStrings) {
        String[] headers = requestStrings.split(Symbol.BODY_DELIMITER)[0].split(Symbol.CRLF);
        return Arrays.copyOfRange(headers, 1, headers.length);
    }
}
