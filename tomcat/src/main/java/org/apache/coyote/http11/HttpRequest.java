package org.apache.coyote.http11;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;

public class HttpRequest {

    private String uri;

    private String method;

    private String requestBody;

    private Cookies cookies;

    public static HttpRequest from(InputStream inputStream) {
        return HttpRequestParser.parseFromSocket(inputStream);
    }

    public HttpRequest(String uri, String method, String requestBody, Cookies cookies) {
        this.uri = uri;
        this.method = method;
        this.requestBody = requestBody;
        this.cookies = cookies;
    }

    public boolean isMethodEqualTo(final String method) {
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
        return isMethodEqualTo("GET") && this.uri.startsWith("/js/");
    }

    public boolean isAssetRequest() {
        return isMethodEqualTo("GET") && this.uri.startsWith("/assets/");
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
        return parseParametersIntoMap(this.requestBody);
    }

    private HashMap<String, String> parseParametersIntoMap(String parameters) {
        final HashMap<String, String> queryParameters = new HashMap<>();
        final StringTokenizer stringTokenizer = new StringTokenizer(parameters, "&");
        while (stringTokenizer.hasMoreTokens()) {
            final String parameter = stringTokenizer.nextToken();
            parseParameterIntoMap(queryParameters, parameter);
        }
        return queryParameters;
    }

    private static void parseParameterIntoMap(final HashMap<String, String> queryParameters, final String parameter) {
        final StringTokenizer stringTokenizer = new StringTokenizer(parameter, "=");
        if (!stringTokenizer.hasMoreTokens()) {
            return;
        }
        queryParameters.put(stringTokenizer.nextToken(), stringTokenizer.nextToken());
    }

    public Optional<String> getSessionId() {
        return cookies.get("JSESSIONID");
    }
}
