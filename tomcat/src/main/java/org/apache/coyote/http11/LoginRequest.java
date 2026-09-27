package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public record LoginRequest(
    String account,
    String password
) {

    public static LoginRequest from(final String requestBody) {
        final Map<String, String> loginParams = new LinkedHashMap<>();
        Arrays.stream(requestBody.split("&"))
            .map(paramToken -> paramToken.split("="))
            .forEach(paramPair -> loginParams.put(paramPair[0], paramPair[1]));

        return new LoginRequest(
            loginParams.get("account"),
            loginParams.get("password"));
    }
}
