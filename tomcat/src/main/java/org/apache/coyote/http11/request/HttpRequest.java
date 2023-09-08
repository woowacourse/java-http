package org.apache.coyote.http11.request;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import org.apache.coyote.http11.resource.Cookies;

public class HttpRequest {

    private String uri;

    private HttpMethod method;

    private String requestBody;

    private Cookies cookies;

    public static HttpRequest from(InputStream inputStream) {
        return HttpRequestParser.parseFromSocket(inputStream);
    }

    public HttpRequest(String uri, String method, String requestBody, Cookies cookies) {
        this.uri = uri;
        this.method = HttpMethod.from(method);
        this.requestBody = requestBody;
        this.cookies = cookies;
    }

    public boolean isMethodEqualTo(final HttpMethod method) {
        return Objects.equals(this.method, method);
    }

    public boolean isUriEqualTo(final String uri) {
        final int startPointOfQueryParameter = this.uri.lastIndexOf("?");
        if (startPointOfQueryParameter != -1) {
            final String uriWithoutQueryParameter = this.uri.substring(0, startPointOfQueryParameter);
            return Objects.equals(uriWithoutQueryParameter, uri);
        }
        return Objects.equals(this.uri, uri);
    }

    public boolean isJavascriptRequest() {
        return isMethodEqualTo(HttpMethod.GET) && this.uri.startsWith("/js/");
    }

    public boolean isAssetRequest() {
        return isMethodEqualTo(HttpMethod.GET) && this.uri.startsWith("/assets/");
    }

    public String getEndPoint() {
        return this.uri.substring(this.uri.lastIndexOf("/") + 1, this.uri.length());
    }

    public Map<String, String> getQueryParameters() {
        if (!hasQueryParameter()) {
            return Map.of();
        }

        final String parameters = this.uri.substring(this.uri.lastIndexOf("?") + 1, this.uri.length());
        return parseParametersIntoMap(parameters);
    }

    public boolean hasQueryParameter() {
        return !Objects.equals(this.uri.lastIndexOf("?"), -1);
    }

    public Optional<String> getQueryParameter(String parameter) {
        return Optional.ofNullable(this.getQueryParameters().get(parameter));
    }

    public Map<String, String> getRequestBodyAsMap() {
        return HttpParameterParser.parseParametersIntoMap(this.requestBody);
    }

    public Optional<String> getSessionId() {
        return cookies.get("JSESSIONID");
    }
}
