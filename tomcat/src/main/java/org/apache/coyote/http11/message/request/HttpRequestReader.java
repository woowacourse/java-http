package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.apache.coyote.http11.message.common.ContentType;
import org.apache.coyote.http11.message.common.HttpHeaders;

public class HttpRequestReader {

    private static final int OFFSET = 0;

    private HttpRequestReader() {
    }

    public static HttpRequest from(BufferedReader reader) throws IOException {
        HttpRequestLine httpRequestLine = new HttpRequestLine(parseStartLine(reader));
        HttpHeaders httpHeaders = new HttpHeaders(parseHeaders(reader));
        ContentType contentType = httpHeaders.getContentType();
        HttpRequestBody httpRequestBody =
                new HttpRequestBody(contentType, parseBody(reader, httpHeaders.getContentLength()));

        return new HttpRequest(httpRequestLine, httpHeaders, httpRequestBody);
    }

    private static String parseStartLine(BufferedReader reader) throws IOException {
        return reader.readLine();
    }

    private static String parseHeaders(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        String line;
        while (StringUtils.isNoneBlank(line = reader.readLine())) {
            builder.append(line).append(System.lineSeparator());
        }
        return builder.toString();
    }

    private static String parseBody(BufferedReader reader, int countLength) throws IOException {
        char[] buffer = new char[countLength];
        reader.read(buffer, OFFSET, countLength);
        return new String(buffer);
    }
}
