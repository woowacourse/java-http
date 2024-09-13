package org.apache.tomcat.util.http.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.tomcat.util.http.header.HttpHeaderType;
import org.apache.tomcat.util.http.header.HttpHeaders;

class HttpRequestHeaderParser {

    private static final String DELIMITER = ":";

    private HttpRequestHeaderParser() {
    }

    protected static HttpHeaders parse(BufferedReader bufferedReader) throws IOException {
        Map<HttpHeaderType, String> headerMap = new HashMap<>();
        String line;
        while (!(line = bufferedReader.readLine()).isEmpty()) {
            StringTokenizer tokenizer = new StringTokenizer(line, DELIMITER);
            String key = tokenizer.nextToken().trim();
            HttpHeaderType headerType = new HttpHeaderType(key);
            String value = tokenizer.nextToken(DELIMITER).trim();
            headerMap.put(headerType, value);
        }
        return new HttpHeaders(headerMap);
    }
}
