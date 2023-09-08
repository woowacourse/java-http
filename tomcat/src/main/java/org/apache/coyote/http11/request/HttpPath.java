package org.apache.coyote.http11.request;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpPath {
    private String path;

    public HttpPath(final String path) {
        this.path = path;
    }

    public static HttpPath from(String path) {
        return new HttpPath(path);
    }

    public boolean isUriEqualTo(final String uri) {
        final int startPointOfQueryParameter = this.path.lastIndexOf("?");
        if (startPointOfQueryParameter != -1) {
            final String uriWithoutQueryParameter = this.path.substring(0, startPointOfQueryParameter);
            return Objects.equals(uriWithoutQueryParameter, uri);
        }
        return Objects.equals(this.path, uri);
    }

    public boolean isJavascriptRequest() {
        return this.path.startsWith("/js/");
    }

    public boolean isAssetRequest() {
        return this.path.startsWith("/assets/");
    }

    public String getEndPoint() {
        return this.path.substring(this.path.lastIndexOf("/") + 1);
    }

    public Map<String, String> getQueryParameters() {
        if (!hasQueryParameter()) {
            return Map.of();
        }

        final String parameters = this.path.substring(this.path.lastIndexOf("?") + 1);
        return HttpParameterParser.parseParametersIntoMap(parameters);
    }

    public boolean hasQueryParameter() {
        return !Objects.equals(this.path.lastIndexOf("?"), -1);
    }

    public Optional<String> getQueryParameter(String parameter) {
        return Optional.ofNullable(this.getQueryParameters().get(parameter));
    }
}
