package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final String method;
    private final String requestUrl;
    private final Map<String, String> requestParams;
    private final String protocolVersion;
    private final Map<String, String> headers;

    private HttpRequest(final String method, final String requestUrl, final Map<String, String> requestParams,
                        final String protocolVersion, final Map<String, String> headers) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.requestParams = requestParams;
        this.protocolVersion = protocolVersion;
        this.headers = headers;
    }

    public static HttpRequest from(final String startLine, final String headers) {
        return new HttpRequest(parseMethod(startLine), parseUrl(startLine), parseRequestParams(startLine),
                parseProtocolVersion(startLine), parseHeaders(headers));
    }

    private static Map<String, String> parseHeaders(final String headers) {
        return Arrays.stream(headers.split("\r\n"))
                .map(header -> header.split(": "))
                .collect(Collectors.toMap(header -> header[0], header -> header[1]));
    }

    private static String parseMethod(final String startLine) {
        return startLine.split(" ")[0];
    }

    private static String parseUrl(final String requestHeader) {
        log.info("requestHeader::: {}", requestHeader);
        return requestHeader.split(" ")[1]
                .split("\\?")[0];
    }

    private static Map<String, String> parseRequestParams(final String requestHeader) {
        String url = requestHeader.split(" ")[1];

        if (!url.contains("?")) {
            return Map.of();
        }

        String params = url.split("\\?")[1];

        return Arrays.stream(params.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));
    }

    private static String parseProtocolVersion(final String startLine) {
        return startLine.split(" ")[2];
    }

    public String getMethod() {
        return method;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public Map<String, String> getRequestParams() {
        return requestParams;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
