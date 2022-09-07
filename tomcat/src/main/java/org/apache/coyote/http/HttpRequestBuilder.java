package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBuilder {

    private static final String DELIMITER = ":";
    private static final String BLANK = " ";
    private static final String EMPTY = "";
    private static final int START = 0;

    public static HttpRequest makeRequest(final BufferedReader bufferedReader) throws IOException {
        final String requestLine = bufferedReader.readLine();
        final Map<String, String> header = makeHeader(bufferedReader);
        final String body = makeBody(bufferedReader,header);
        return new HttpRequest(requestLine, new Header(header) , body);
    }

    private static Map<String, String> makeHeader(BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headerMap = new HashMap<>();
        String reader;
        while ((reader = bufferedReader.readLine()) != null) {
            if(reader.equals(EMPTY)){
                break;
            }
            final Integer index = reader.indexOf(DELIMITER);
            headerMap.put(reader.substring(START, index), reader.substring(index + 1).replace(BLANK, EMPTY));
        }
        return headerMap;
    }

    private static String makeBody(final BufferedReader bufferedReader,
                                   Map<String, String> headerMap) throws IOException {
        final String contentLength = headerMap.get("Content-Length");
        if (!hasBody(contentLength)) {
            return EMPTY;
        }
        final int contentLengthSize = Integer.parseInt(contentLength);
        final char[] body = new char[contentLengthSize];
        bufferedReader.read(body, 0, contentLengthSize);
        return new String(body);
    }

    private static boolean hasBody(final String contentLength) {
        return contentLength != null;
    }
}
