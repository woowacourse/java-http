package nextstep.jwp.framework.request;

import nextstep.jwp.framework.request.details.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

public class HttpRequest {

    private static final String REQUEST_SEPARATOR = " ";

    private final HttpMethod httpMethod;
    private final RequestUrl requestUrl;
    private final ProtocolVersion protocolVersion;
    private final RequestHttpHeader requestHttpHeader;
    private final RequestBody requestBody;

    private HttpRequest(final HttpMethod httpMethod, final RequestUrl requestUrl, final ProtocolVersion protocolVersion,
                        final RequestHttpHeader requestHttpHeader, final RequestBody requestBody) {
        this.httpMethod = httpMethod;
        this.requestUrl = requestUrl;
        this.protocolVersion = protocolVersion;
        this.requestHttpHeader = requestHttpHeader;
        this.requestBody = requestBody;
    }

    public static HttpRequest of(final HttpMethod httpMethod, final String url) {
        return new HttpRequest(
                httpMethod,
                RequestUrl.of(url),
                ProtocolVersion.defaultVersion(),
                null,
                null);
    }

    public static HttpRequest from(final String requestLine, final Map<String, String> requestHttpHeader,
                                   final String requestBody) {
        final String[] requestInfos = requestLine.split(REQUEST_SEPARATOR);
        return new HttpRequest(
                HttpMethod.of(requestInfos[0]),
                RequestUrl.of(requestInfos[1]),
                ProtocolVersion.of(requestInfos[2]),
                RequestHttpHeader.of(requestHttpHeader),
                RequestBody.of(requestBody));
    }

    public RequestHttpHeader getRequestHttpHeader() {
        return requestHttpHeader;
    }

    public String searchRequestBody(final String key) {
        return requestBody.find(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequest that = (HttpRequest) o;
        return Objects.equals(httpMethod, that.httpMethod) &&
                Objects.equals(requestUrl, that.requestUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, requestUrl);
    }
}
