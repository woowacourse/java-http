package org.apache.coyote.http.request;

import java.util.Arrays;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class HttpParameterDecoder {

    private HttpParameterDecoder() {
    }

    public static Map<String, String> decode(String parametersString) {
        return Arrays.stream(parametersString.split("&"))
                .map(parameterString -> parameterString.split("="))
                .filter(keyValue -> keyValue.length == 2)
                .collect(toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
    }
}
