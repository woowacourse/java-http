package org.apache.coyote.http11.common;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.exception.HttpFormatException;

public class HttpParser {

    public static Map<String, String> parseHeaders(final String message) {
        final var header = new HashMap<String, String>();
        if (message.isBlank()) {
            return header;
        }
        for (var line : message.split("\r\n")) {
            parseEachHeader(line, header);
        }
        return header;
    }

    private static void parseEachHeader(final String line, final HashMap<String, String> header) {
        try {
            final var index = line.indexOf(":");
            header.put(line.substring(0, index).trim(), line.substring(index + 1).trim());
        } catch (StringIndexOutOfBoundsException e) {
            throw new HttpFormatException("HTTP Request Header 형식이 올바르지 않습니다.");
        }
    }

    public static Map<String, String> parseCookie(final String cookie) {
        final var options = new HashMap<String, String>();
        if (cookie == null || cookie.isBlank()) {
            return options;
        }
        for (var option : cookie.split(";")) {
            parseCookieOption(option.trim(), options);
        }
        return options;
    }

    private static void parseCookieOption(String option, HashMap<String, String> options) {
        try {
            final var index = option.indexOf("=");
            options.put(option.substring(0, index).trim(), option.substring(index + 1).trim());
        } catch (StringIndexOutOfBoundsException e) {
            throw new HttpFormatException("HTTP Cookie 형식에 맞지 않습니다.");
        }
    }

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
