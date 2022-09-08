package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.util.UrlUtil;

public class RequestPath {

    private final String path;
    private final Map<String, String> queryParameters;

    public RequestPath(String path, Map<String, String> queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public static RequestPath parse(String pathString) {
        String path = UrlUtil.parsePath(pathString);
        Map<String, String> queryParameters = UrlUtil.parseQueryString(pathString);
        return new RequestPath(path, queryParameters);
    }

    public String getPath() {
        return path;
    }

    public String getParameter(String field) {
        return queryParameters.get(field);
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    @Override
    public String toString() {
        return "RequestPath{\n" +
                "path='" + path + '\'' +
                ", queryParameters=" + queryParameters +
                "\n}";
    }
}
