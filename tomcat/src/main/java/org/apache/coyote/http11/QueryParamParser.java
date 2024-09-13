package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryParamParser {

    private static String PARAM_DELIMITER = "&";
    private static String VALUE_DELIMITER = "=";

    private final Parameter params;

    public QueryParamParser(String url) {
        String queryString = extractQueryString(url);
        params = new Parameter(parse(queryString));
    }

    private Map<String, String> parse(String queryString) {
        return Arrays.stream(queryString.split(PARAM_DELIMITER))
                .map(params -> params.split(VALUE_DELIMITER))
                .filter(tokens -> tokens.length == 2)
                .collect(Collectors.toMap(
                        tokens -> tokens[0],
                        tokens -> tokens[1]));
    }

    private String extractQueryString(String url) {
        String rawEncodedQueryString = parseQueryString(url);
        return URLDecoder.decode(rawEncodedQueryString, StandardCharsets.UTF_8);
    }

    private String parseQueryString(String uri) {
        int index = uri.indexOf("?");
        return uri.substring(index + 1);
    }

    public Parameter getParameter() {
        return params;
    }
}
