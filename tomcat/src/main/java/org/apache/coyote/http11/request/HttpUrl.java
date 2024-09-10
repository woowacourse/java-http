package org.apache.coyote.http11.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class HttpUrl {

    private final String path;
    private final Map<String, String> queryParameters;

    public HttpUrl(String url) {
        String[] urlComponents = parseUrl(url);
        this.path = urlComponents[0];
        this.queryParameters = createParameters(urlComponents);
    }

    public HttpUrl(String path, Map<String, String> queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    private String[] parseUrl(String url) {
        String[] urlComponents = url.split("\\?");
        if (urlComponents.length == 1 || urlComponents.length == 2) {
            return urlComponents;
        }
        throw new UncheckedServletException("URL 형식이 일치하지 않습니다");
    }

    private Map<String, String> createParameters(String[] urlComponents) {
        if (urlComponents.length == 1) {
            return Collections.EMPTY_MAP;
        }
        String queryParameters = urlComponents[1];
        String[] parameters = queryParameters.split("&");
        Map<String, String> queryParametersMap = new HashMap<>();
        for (String queryParameter : parameters) {
            createParameter(queryParameter)
                    .ifPresent(entry -> queryParametersMap.put(entry.getKey(), entry.getValue()));
        }
        return queryParametersMap;
    }

    private Optional<Entry<String, String>> createParameter(String queryParameter) {
        String[] parameterComponents = queryParameter.split("=");
        if (parameterComponents.length == 2) {
            Entry<String, String> parameter = Map.entry(parameterComponents[0], parameterComponents[1]);
            return Optional.of(parameter);
        }
        return Optional.empty();
    }

    public String getPath() {
        return path;
    }

    public Optional<String> getParameter(String key) {
        return Optional.ofNullable(queryParameters.get(key));
    }

    public Map<String, String> getParameters() {
        return queryParameters;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HttpUrl httpUrl = (HttpUrl) object;
        return Objects.equals(path, httpUrl.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpUrl.class.getSimpleName() + "[", "]")
                .add("path='" + path + "'")
                .toString();
    }
}
