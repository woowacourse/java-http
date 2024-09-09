package org.apache.coyote.http11.request.body.parser;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FormUrlEncodedParser implements BodyParser {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String PARAMETER_SEPARATOR = "=";

    private static final int VALID_PARAMETER_PAIR_LENGTH = 2;
    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    @Override
    public Map<String, String> parse(String body) {
        return Arrays.stream(body.split(PARAMETER_DELIMITER))
                .map(params -> params.split(PARAMETER_SEPARATOR))
                .filter(parts -> parts.length == VALID_PARAMETER_PAIR_LENGTH)
                .collect(Collectors.toMap(
                        parts -> URLDecoder.decode(parts[NAME_INDEX], StandardCharsets.UTF_8),
                        parts -> URLDecoder.decode(parts[VALUE_INDEX], StandardCharsets.UTF_8)
                ));
    }
}
