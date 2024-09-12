package org.apache.coyote.http11.request.body.parser;

import static org.apache.coyote.http11.Constants.NAME_INDEX;
import static org.apache.coyote.http11.Constants.VALID_PARAMETER_PAIR_LENGTH;
import static org.apache.coyote.http11.Constants.VALUE_INDEX;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class FormUrlEncodedParser implements BodyParser {

    private static final String PARAMETER_DELIMITER = "&";
    private static final String PARAMETER_SEPARATOR = "=";

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
