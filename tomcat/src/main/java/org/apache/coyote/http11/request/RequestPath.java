package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestPath {

    private static final Pattern PATH_PATTERN = Pattern.compile("(?<path>/[a-zA-Z0-9\\-./]*)[?]?");
    private static final Pattern QUERY_PARAMETER_PATTERN
            = Pattern.compile("((?<field>[a-zA-Z0-9\\-]+)=(?<value>[a-zA-Z0-9\\-]+))&?");

    private final String path;
    private final Map<String, String> queryParameters;

    public RequestPath(String path, Map<String, String> queryParameters) {
        this.path = path;
        this.queryParameters = queryParameters;
    }

    public static RequestPath parse(String pathString) {
        String path = parsePath(pathString);
        Map<String, String> queryParameters = parseQueryParameters(pathString);
        return new RequestPath(path, queryParameters);
    }

    private static String parsePath(String pathString) {
        Matcher matcher = PATH_PATTERN.matcher(pathString);
        if (!matcher.find()) {
            throw new RuntimeException();
        }
        return matcher.group("path");
    }

    private static Map<String, String> parseQueryParameters(String pathString) {
        Matcher matcher = QUERY_PARAMETER_PATTERN.matcher(pathString);
        Map<String, String> queryParameters = new HashMap<>();
        while (matcher.find()) {
            queryParameters.put(matcher.group("field"), matcher.group("value"));
        }
        return queryParameters;
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
