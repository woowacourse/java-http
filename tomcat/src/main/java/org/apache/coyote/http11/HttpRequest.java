package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.header.HttpHeader;

public class HttpRequest {

    private static final String URLENCODED_DELIMITER = "&";
    private static final String URLENCODED_VALUE_DELIMITER = "=";
    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_PATH_INDEX = 1;
    private static final int URLENCODED_NAME_INDEX = 0;
    private static final int URLENCODED_VALUE_INDEX = 1;

    private final HttpMethod method;
    private final String requestUrl;
    private final HttpHeader headers;
    private final HttpCookie httpCookie;
    private final Map<String, String> body;

    private HttpRequest(
            HttpHeader header,
            HttpMethod method,
            String requestUrl,
            HttpCookie httpCookie,
            Map<String, String> body
    ) {
        this.headers = header;
        this.method = method;
        this.requestUrl = requestUrl;
        this.httpCookie = httpCookie;
        this.body = body;
    }

    public static HttpRequest of(List<String> header, String rawBody) {
        String[] requestLine = header.getFirst().split(REQUEST_LINE_DELIMITER);
        HttpMethod method = HttpMethod.valueOf(requestLine[REQUEST_METHOD_INDEX]);
        String requestUrl = requestLine[REQUEST_PATH_INDEX];

        HttpHeader httpHeader = HttpHeader.from(header);
        HttpCookie httpCookie = HttpCookie.from(httpHeader);
        Map<String, String> body = parseBody(rawBody);

        return new HttpRequest(httpHeader, method, requestUrl, httpCookie, body);
    }

    private static Map<String, String> parseBody(String rawBody) {
        if (rawBody.isEmpty()) {
            return new HashMap<>();
        }

        return Arrays.stream(rawBody.split(URLENCODED_DELIMITER))
                .collect(Collectors.toMap(
                        s -> s.split(URLENCODED_VALUE_DELIMITER)[URLENCODED_NAME_INDEX],
                        s -> s.split(URLENCODED_VALUE_DELIMITER)[URLENCODED_VALUE_INDEX])
                );
    }

    public boolean isGetRequest() {
        return method.isGet();
    }

    public boolean isPostRequest() {
        return method.isPost();
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getBody(String key) {
        return body.get(key);
    }

    public HttpCookie getCookie() {
        return httpCookie;
    }
}
