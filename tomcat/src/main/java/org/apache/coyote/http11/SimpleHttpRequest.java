package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleHttpRequest {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final String requestMessage;

    public SimpleHttpRequest(final String requestMessage) {
        validateConnection(requestMessage);
        this.requestMessage = requestMessage;
    }

    private void validateConnection(final String requestMessage) {
        if (requestMessage == null) {
            throw new IllegalArgumentException("Http 요청 메시지로 null을 입력할 수 없습니다.");
        }
    }

    public String getRequestMessage() {
        return requestMessage;
    }

    public HttpMethod getHttpMethod() {
        final int httpMethodIndexNumber = 0;
        final String httpMethodValue = parseRequestLine().split(" ")[httpMethodIndexNumber];
        return HttpMethod.valueOf(httpMethodValue.toUpperCase());
    }

    public String getRequestUri() {
        final int requestUriIndexNumber = 1;
        return parseRequestLine().split(" ")[requestUriIndexNumber];
    }

    public String getEndpoint() {
        return getRequestUri().split("\\?")[0];
    }

    public Map<String, String> getQueryParameters() {
        Map<String, String> queryParameters = new HashMap<>();
        final String[] endPointAndQueryParams = getRequestUri().split("\\?");
        if (endPointAndQueryParams.length < 2) {
            return queryParameters;
        }
        final String[] queryParams = endPointAndQueryParams[1].split("&");
        Arrays.stream(queryParams)
                .forEach(param -> {
                    final String[] keyAndValue = param.split("=");
                    queryParameters.put(keyAndValue[0], keyAndValue[1]);
                });
        return queryParameters;
    }

    private String parseRequestLine() {
        return requestMessage.lines()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("유효하지 않은 요청 메시지여서 처리할 수 없습니다."));
    }

    public FileExtensionType parseStaticFileExtensionType() {
        final String requestUri = getRequestUri();
        log.info("requestUri: {}", requestUri);
        final String[] split = requestUri.split("\\.");
        log.info("split = {}", Arrays.toString(split));
        if (split.length != 2) {
            return null;
        }

        return FileExtensionType.fromValue(split[1]);
    }

    public Map<String, String> getBodyData() {
        final HashMap<String, String> result = new HashMap<>();
        final String[] headerAndBodyData = requestMessage.split("\r\n\r\n", 2);
        if (headerAndBodyData.length < 2) {
            return Collections.emptyMap();
        }

        final String[] bodyData = headerAndBodyData[1].split("&");
        Arrays.stream(bodyData)
                .forEach(data -> {
                    final String[] keyAndValue = data.trim().split("=");
                    result.put(keyAndValue[0], keyAndValue[1]);
                });

        return result;
    }

    public Map<String, String> getCookies() {
        final String cookieLinePrefix = "Cookie: ";
        final String cookieLine = Arrays.stream(requestMessage.split("\r\n"))
                .filter(line -> line.startsWith(cookieLinePrefix))
                .findAny()
                .orElse(null);

        if (cookieLine == null) {
            return Collections.emptyMap();
        }
        final String[] cookies = cookieLine.replace(cookieLinePrefix, "")
                .trim()
                .split("; ");

        final HashMap<String, String> result = new HashMap<>();
        Arrays.stream(cookies)
                .forEach(cookie -> {
                    final String[] keyAndValue = cookie.split("=");
                    result.put(keyAndValue[0], keyAndValue[1]);
                });

        return result;
    }
}
