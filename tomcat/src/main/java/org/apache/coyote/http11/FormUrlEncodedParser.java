package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FormUrlEncodedParser {

    private static String PARAM_DELIMITER = "&";
    private static String VALUE_DELIMITER = "=";

    private final Parameter params;

    public FormUrlEncodedParser(String urlEncodedBody) {
        this.params = new Parameter(parse(urlEncodedBody));
    }

    public Parameter getParameter() {
        return params;
    }

    private Map<String, String> parse(String urlEncodedBody) {
        String queryString = URLDecoder.decode(urlEncodedBody, StandardCharsets.UTF_8);
        return Arrays.stream(queryString.split(PARAM_DELIMITER))
                .map(params -> params.split(VALUE_DELIMITER))
                .filter(tokens -> tokens.length == 2)
                .collect(Collectors.toMap(
                        tokens -> tokens[0],
                        tokens -> tokens[1]));
    }
}
