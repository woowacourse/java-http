package org.apache.coyote.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http.HttpHeader;

public class HttpRequest {

    private static final String LINE_SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URL_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpRequestLine httpRequestLine;
    private final HttpHeader httpHeaders;

    public HttpRequest(final HttpRequestLine httpRequestLine, final HttpHeader httpHeaders) {
        this.httpRequestLine = httpRequestLine;
        this.httpHeaders = httpHeaders;
    }

    public boolean hasQueryString() {
        return httpRequestLine.hasQueryParams();
    }

    public String getUrl() {
        return httpRequestLine.getPath();
    }

    public Map<String, String> getQueryString() {
        return httpRequestLine.getQueryParams();
    }

    public static HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        return new HttpRequest(createHttpRequestLine(bufferedReader), createHttpRequestHeader(bufferedReader));
    }

    private static HttpHeader createHttpRequestHeader(final BufferedReader bufferedReader) throws IOException {
        List<String> headerList = new ArrayList<>();
        final String line = bufferedReader.readLine();
        while (line.isBlank()) {
            headerList.add(bufferedReader.readLine());
        }
        return HttpHeader.from(headerList);
    }

    private static HttpRequestLine createHttpRequestLine(final BufferedReader bufferedReader) throws IOException {
        final String[] line = bufferedReader.readLine().split(LINE_SEPARATOR);
        return HttpRequestLine.from(line[METHOD_INDEX], line[URL_INDEX], line[VERSION_INDEX]);
    }
}
