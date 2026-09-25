package org.apache.coyote.http11.request;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class HttpParameters {

    private final Map<String, String> parameters;

    private HttpParameters(Map<String, String> parameters) {
        this.parameters = Map.copyOf(parameters);
    }

    public static HttpParameters from(String rawParameters) throws IOException {
        Map<String, String> parameters = new HashMap<>();
        StringTokenizer stringTokenizer = new StringTokenizer(rawParameters, "&");

        while (stringTokenizer.hasMoreTokens()) {
            String parameter = stringTokenizer.nextToken();
            validateKeyValue(parameter);
            int index = parameter.indexOf("=");
            String key = URLDecoder.decode(parameter.substring(0, index), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(parameter.substring(index + 1), StandardCharsets.UTF_8);
            parameters.put(key, value);
        }
        return new HttpParameters(parameters);
    }

    public static HttpParameters empty() {
        return new HttpParameters(Map.of());
    }

    public boolean containsKey(String key) {
        return parameters.containsKey(key);
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    private static void validateKeyValue(String parameter) throws IOException {
        if (!parameter.contains("=")) {
            throw new IOException("Key=Value 형태가 아닙니다: " + parameter);
        }
    }
}
