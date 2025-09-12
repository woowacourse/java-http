package org.apache.coyote.http11.request;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.apache.coyote.http11.request.util.HttpRequestIO;

@Getter
public class HttpRequest {

    private final RequestLine requestLine;
    private final RequestHeaders requestHeaders;
    private final RequestCookies requestCookies;
    private final Map<String, String> parameters;
    private final String requestBody;

    private HttpRequest(final RequestLine requestLine, final RequestHeaders requestHeaders,
                        final RequestCookies requestCookies, final Map<String, String> parameters,
                        final String requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestCookies = requestCookies;
        this.parameters = Collections.unmodifiableMap(parameters);
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final InputStream inputStream) throws IOException {
        final var headerReader = HttpRequestIO.createHeaderReader(inputStream);
        final var requestLine = RequestLine.from(headerReader.readLine());
        final var requestHeaders = RequestHeaders.from(headerReader);
        final var requestCookies = RequestCookies.from(requestHeaders.getHeader("Cookie"));

        final Map<String, String> parameters = new HashMap<>(requestLine.getQueryParameters());

        String requestBody = "";
        if (requestLine.getMethod() == HttpMethod.POST) {
            requestBody = HttpRequestIO.readRequestBody(requestHeaders, inputStream);
            parameters.putAll(RequestBodyUtils.parseFormUrlEncoded(requestBody));
        }

        return new HttpRequest(requestLine, requestHeaders, requestCookies, parameters, requestBody);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }
}
