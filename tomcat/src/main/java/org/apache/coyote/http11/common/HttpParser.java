package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.exception.HttpFormatException;

public class HttpParser {

    public static Map<String, String> parseQueryString(final String message) {
        final var parameters = new HashMap<String, String>();
        for (var parameter : message.split("&")) {
            parseEachParameter(parameters, parameter);
        }
        return parameters;
    }

    private static void parseEachParameter(HashMap<String, String> parameters, String parameter) {
        try {
            final var index = parameter.indexOf("=");
            parameters.put(parameter.substring(0, index).trim(), parameter.substring(index + 1).trim());
        } catch (StringIndexOutOfBoundsException e) {
            throw new HttpFormatException("Query String 형식이 올바르지 않습니다.");
        }
    }
}
