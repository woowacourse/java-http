package org.apache.coyote.http11;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpRequest {

    private String uri;

    private String method;

    private String requestBody;

    public static HttpRequest from(Socket connection) {
        return HttpRequestParser.parseFromSocket(connection);
    }

    public HttpRequest(String uri, String method, String requestBody) {
        this.uri = uri;
        this.method = method;
        this.requestBody = requestBody;
    }

    public boolean isMethodEqualTo(final String method) {
        return Objects.equals(this.method, method);
    }

    public boolean isUriEqualTo(final String uri) {
        final int startpointOfQueryParameter = this.uri.lastIndexOf("?");
        if (startpointOfQueryParameter != -1) {
            final String uriWithoutQueryParameter = this.uri.substring(0, startpointOfQueryParameter);
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
        return parseParameterAsMap(parameters);
    }

    public boolean hasQueryParameter() {
        return !Objects.equals(this.uri.lastIndexOf("?"), -1);
    }

    public Optional<String> getQueryParameter(String parameter) {
        return Optional.ofNullable(this.getQueryParameters().get(parameter));
    }

    public Map<String, String> getRequestBodyAsMap() {
        return parseParameterAsMap(this.requestBody);
    }

    private HashMap<String, String> parseParameterAsMap(String parameters) {
        final HashMap<String, String> queryParameters = new HashMap<>();
        for (String parameter : parameters.split("&")) {
            if (isInValidParameter(parameter)) {
                continue;
            }
            final String key = parameter.split("=")[0];
            final String value = parameter.split("=")[1];
            queryParameters.put(key, value);
        }
        return queryParameters;
    }

    private boolean isInValidParameter(String parameter) {
        return Objects.isNull(parameter) || !Objects.equals(parameter.split("=").length, 2);
    }
}
