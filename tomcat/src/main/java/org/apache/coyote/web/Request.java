package org.apache.coyote.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.exception.HttpRequestStartLineNotValidException;
import org.apache.coyote.support.HttpHeader;
import org.apache.coyote.support.HttpHeaderFactory;
import org.apache.coyote.support.HttpHeaderFactory.Pair;
import org.apache.coyote.support.HttpHeaders;
import org.apache.coyote.support.HttpMethod;

public class Request {

    private static final String START_LINE_DELIMITER = " ";
    private static final String EXTENSION_DELIMITER = "\\.";
    private static final int REQUEST_LINE_LENGTH = 3;
    private static final int NO_SPLIT_SIGN = 1;
    private static final String DEFAULT_REQUEST_EXTENSION = "strings";
    private static final String QUERY_PARAMETER_DELIMITER = "\\?";
    private static final String HEADER_DELIMITER = ": ";
    private static final String CONNECT_DELIMITER = "&";
    private static final String ASSIGN_DELIMITER = "=";

    private final HttpMethod method;
    private final String requestUrl;
    private final String version;
    private final HttpHeaders httpHeaders;
    private final String requestBody;

    public Request(final String method,
                   final String requestUrl,
                   final String version,
                   final HttpHeaders httpHeaders,
                   final String requestBody) {
        this.method = HttpMethod.of(method);
        this.requestUrl = requestUrl;
        this.version = version;
        this.httpHeaders = httpHeaders;
        this.requestBody = requestBody;
    }

    public static Request parse(final BufferedReader bufferedReader) throws IOException {
        String[] requestLine = bufferedReader.readLine().split(START_LINE_DELIMITER);
        if (requestLine.length != REQUEST_LINE_LENGTH) {
            throw new HttpRequestStartLineNotValidException();
        }

        List<Pair> pairs = bufferedReader.lines()
                .takeWhile(line -> !"".equals(line))
                .map(line -> Pair.splitBy(line, HEADER_DELIMITER))
                .collect(Collectors.toList());
        HttpHeaders httpHeaders = HttpHeaderFactory.create(pairs.toArray(Pair[]::new));

        int contentLength = Integer.parseInt(httpHeaders.getValueOrDefault(HttpHeader.CONTENT_LENGTH.getValue(), "0"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new Request(requestLine[0], requestLine[1], requestLine[2], httpHeaders, new String(buffer));
    }

    public String getRequestExtension() {
        String[] nameExtension = splitRequestUrlWith(EXTENSION_DELIMITER);
        if (nameExtension.length == NO_SPLIT_SIGN) {
            return DEFAULT_REQUEST_EXTENSION;
        }
        return nameExtension[1];
    }

    public Map<String, String> getQueryParameters() {
        String[] requestSplit = requestUrl.split(QUERY_PARAMETER_DELIMITER);
        if (requestSplit.length == NO_SPLIT_SIGN) {
            return Collections.emptyMap();
        }
        String queryParameters = requestSplit[1];
        return doParse(queryParameters);
    }

    private String getSplitValue(final String content, final String delimiter, final int index) {
        return content.split(delimiter)[index];
    }

    public boolean isFileRequest() {
        return splitRequestUrlWith(EXTENSION_DELIMITER).length != NO_SPLIT_SIGN;
    }

    public boolean isSameRequestUrl(final String url) {
        return splitRequestUrlWith(QUERY_PARAMETER_DELIMITER)[0].equals(url);
    }

    private String[] splitRequestUrlWith(final String delimiter) {
        return requestUrl.split(delimiter);
    }

    public Map<String, String> parseBody() {
        return doParse(requestBody);
    }

    private Map<String, String> doParse(final String content) {
        return Arrays.stream(content.split(CONNECT_DELIMITER))
                .collect(Collectors.toMap(parameter -> getSplitValue(parameter, ASSIGN_DELIMITER, 0),
                        parameter -> getSplitValue(parameter, ASSIGN_DELIMITER, 1)));
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getVersion() {
        return version;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public String getRequestBody() {
        return requestBody;
    }
}
