package nextstep.jwp.framework.http.request;

import nextstep.jwp.framework.http.common.HttpSession;
import nextstep.jwp.framework.http.common.ProtocolVersion;
import nextstep.jwp.framework.http.request.details.HttpMethod;
import nextstep.jwp.framework.http.request.details.RequestBody;
import nextstep.jwp.framework.http.request.details.RequestHttpHeader;
import nextstep.jwp.framework.http.request.details.RequestUrl;

import java.util.Map;
import java.util.Objects;

import static nextstep.jwp.framework.http.common.Constants.LINE_SEPARATOR;

public class HttpRequest {

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
        final String[] requestInfos = requestLine.split(LINE_SEPARATOR);
        return new HttpRequest(
                HttpMethod.of(requestInfos[0]),
                RequestUrl.of(requestInfos[1]),
                ProtocolVersion.of(requestInfos[2]),
                RequestHttpHeader.of(requestHttpHeader),
                RequestBody.asQueryString(requestBody));
    }

    public String searchRequestBody(final String key) {
        return requestBody.find(key);
    }

    public HttpSession generateSession() {
        return requestHttpHeader.getCookie().generateSession();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestUrl getRequestUrl() {
        return requestUrl;
    }

    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    public RequestHttpHeader getRequestHttpHeader() {
        return requestHttpHeader;
    }

    public RequestBody getRequestBody() {
        return requestBody;
    }

    public HttpSession getSession() {
        return requestHttpHeader.getCookie().getSession();
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
