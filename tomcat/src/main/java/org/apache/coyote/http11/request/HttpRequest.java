package org.apache.coyote.http11.request;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.resource.Cookies;

public class HttpRequest {

    private final HttpPath uri;

    private final HttpMethod method;

    private final String requestBody;

    private final Cookies cookies;

    public static HttpRequest from(InputStream inputStream) {
        return HttpRequestParser.parseFromSocket(inputStream);
    }

    public HttpRequest(String uri, String method, String requestBody, Cookies cookies) {
        this.uri = HttpPath.from(uri);
        this.method = HttpMethod.from(method);
        this.requestBody = requestBody;
        this.cookies = cookies;
    }

    public boolean isMethodEqualTo(final HttpMethod method) {
        return Objects.equals(this.method, method);
    }

    public boolean isUriEqualTo(final String uri) {
        return this.uri.isUriEqualTo(uri);
    }

    public boolean isJavascriptRequest() {
        return isMethodEqualTo(HttpMethod.GET) && this.uri.isJavascriptRequest();
    }

    public boolean isAssetRequest() {
        return isMethodEqualTo(HttpMethod.GET) && this.uri.isAssetRequest();
    }

    public String getEndPoint() {
        return this.uri.getEndPoint();
    }

    public Map<String, String> getQueryParameters() {
        return this.uri.getQueryParameters();
    }

    public boolean hasQueryParameter() {
        return this.uri.hasQueryParameter();
    }

    public Optional<String> getQueryParameter(String parameter) {
        return this.uri.getQueryParameter(parameter);
    }

    public Map<String, String> getRequestBodyAsMap() {
        return HttpParameterParser.parseParametersIntoMap(this.requestBody);
    }

    public Optional<String> getSessionId() {
        return cookies.get("JSESSIONID");
    }
}
