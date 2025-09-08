package org.apache.catalina.request;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Parameters {

    private static final String PAIR_DELIMITER = "=";
    private static final char PATH_DELIMITER = '?';
    private static final String PARAM_DELIMITER = "&";
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";

    private final Map<String, String> parameters;

    public Parameters(String uri, String body, String contentType) {
        this.parameters = new HashMap<>();
        parameters.putAll(parseQuery(uri));
        parameters.putAll(parseFormBody(body, contentType));
    }

    public String getParameter(String name) {
        return parameters.get(name);
    }

    private Map<String, String> parseQuery(String uri) {
        int queryIndex = uri.indexOf(PATH_DELIMITER);

        if (queryIndex == -1) {
            return Collections.emptyMap();
        }

        return parseParameters(uri.substring(queryIndex + 1));
    }

    private Map<String, String> parseParameters(String paramString) {
        return Arrays.stream(paramString.split(PARAM_DELIMITER))
                .filter(pair -> pair.contains(PAIR_DELIMITER))
                .collect(Collectors.toMap(
                        pair -> URLDecoder.decode(
                                pair.substring(0, pair.indexOf(PAIR_DELIMITER)),
                                StandardCharsets.UTF_8
                        ),
                        pair -> URLDecoder.decode(
                                pair.substring(pair.indexOf(PAIR_DELIMITER) + 1),
                                StandardCharsets.UTF_8
                        )
                ));
    }

    private Map<String, String> parseFormBody(String body, String contentType) {
        if (contentType == null || !contentType.contains(FORM_URLENCODED)) {
            return Collections.emptyMap();
        }

        return parseParameters(body);
    }
}
